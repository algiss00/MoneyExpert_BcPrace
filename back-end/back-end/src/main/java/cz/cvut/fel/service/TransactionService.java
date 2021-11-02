package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeCurrency;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.dto.TypeTransaction;
import cz.cvut.fel.model.*;
import cz.cvut.fel.service.exceptions.NotValidDataException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class TransactionService extends AbstractServiceHelper {
    public TransactionService(UserDao userDao, BankAccountDao bankAccountDao, TransactionDao transactionDao,
                              BudgetDao budgetDao, DebtDao debtDao, CategoryDao categoryDao,
                              NotifyBudgetDao notifyBudgetDao, NotifyDebtDao notifyDebtDao) {
        super(userDao, bankAccountDao, transactionDao, budgetDao, debtDao, categoryDao, notifyBudgetDao, notifyDebtDao);
    }

    public List<Transaction> getAll() {
        return transactionDao.findAll();
    }

    public List<Transaction> getSortedByMonth(int month, int year, int bankAccId) throws Exception {
        return transactionDao.getByMonthSorted(month, year, getByIdBankAccount(bankAccId).getId());
    }

    public double getSumOfExpenseOnMonth(int month, int year, int bankAccId) throws Exception {
        return transactionDao.getExpenseSum(month, year, getByIdBankAccount(bankAccId).getId());
    }

    public double getSumOfIncomeOnMonth(int month, int year, int bankAccId) throws Exception {
        return transactionDao.getIncomeSum(month, year, getByIdBankAccount(bankAccId).getId());
    }

    public double getSumOfExpenseWithCategory(int month, int year, int bankAccId, int catId)
            throws Exception {
        return transactionDao.getExpenseSumWithCategory(month, year, getByIdBankAccount(bankAccId).getId(), getByIdCategory(catId).getId());
    }

    public double getSumOfIncomeWithCategory(int month, int year, int bankAccId, int catId)
            throws Exception {
        return transactionDao.getIncomeSumWithCategory(month, year, getByIdBankAccount(bankAccId).getId(), getByIdCategory(catId).getId());
    }

    public List<Transaction> getAllTransFromCategoryFromBankAcc(int catId, int accountId) throws
            Exception {
        return transactionDao.getAllTransFromCategory(getByIdCategory(catId).getId(), getByIdBankAccount(accountId).getId());
    }

    public List<Transaction> getBetweenDate(String strFrom, String strTo, int accountId) throws Exception {
        return transactionDao.getBetweenDate(strFrom, strTo, getByIdBankAccount(accountId).getId());
    }

    public Transaction persist(Transaction transaction, int accId, int categoryId) throws Exception {
        Objects.requireNonNull(transaction);
        if (!validate(transaction))
            throw new NotValidDataException("transaction");

        BankAccount b = getByIdBankAccount(accId);
        Category category = getByIdCategory(categoryId);
        transaction.setBankAccount(b);
        transaction.setCategory(category);
        transaction.setDate(new Date());
        Transaction persistedTransaction = transactionDao.persist(transaction);

        b.getTransactions().add(transaction);
        bankAccountLogic(b, transaction);
        bankAccountDao.update(b);
        return persistedTransaction;
    }

    private boolean validate(Transaction t) {
        if (t.getTypeTransaction() == null || t.getAmount() <= 0) {
            return false;
        }
        return true;
    }

    private void bankAccountLogic(BankAccount bankAccount, Transaction transaction) throws Exception {
        if (transaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            bankAccount.setBalance(bankAccount.getBalance() - transaction.getAmount());
            budgetLogic(bankAccount, transaction);
        }
        if (transaction.getTypeTransaction() == TypeTransaction.INCOME) {
            bankAccount.setBalance(bankAccount.getBalance() + transaction.getAmount());
        }
    }

    public Transaction transferTransaction(int fromAccId, int toAccId, int transactionId) throws Exception {
        Transaction transaction = getByIdTransaction(transactionId);
        Transaction transferTransaction = new Transaction();

        double actualCurrencyAmount = transaction.getAmount();
        BankAccount toBankAcc = getByIdBankAccount(toAccId);
        BankAccount fromBankAcc = getByIdBankAccount(fromAccId);

        if (!isTransactionInBankAcc(fromAccId, transactionId)) {
            throw new NotValidDataException();
        }

        transferTransaction.setBankAccount(toBankAcc);
        transferTransaction.setCategory(transaction.getCategory());
        if (transaction.getJottings().isEmpty()) {
            transferTransaction.setJottings("Transfer transaction from " + fromBankAcc.getName());
        } else {
            transferTransaction.setJottings(transaction.getJottings());
        }

        if (toBankAcc.getCurrency() != fromBankAcc.getCurrency()) {
            actualCurrencyAmount = currencyConvertLogic(transaction.getAmount(), toBankAcc.getCurrency());
        }
        transferTransaction.setAmount(actualCurrencyAmount);
        transferTransaction.setDate(new Date());
        transferTransaction.setTypeTransaction(transaction.getTypeTransaction());

        Transaction persistedTransaction = transactionDao.persist(transferTransaction);
        toBankAcc.getTransactions().add(transferTransaction);
        bankAccountLogic(toBankAcc, transferTransaction);
        bankAccountDao.update(toBankAcc);

        removeTransFromAccount(transaction.getId(), fromAccId);
        return persistedTransaction;
    }

    private double currencyConvertLogic(double amount, TypeCurrency currency) {
        if (currency == TypeCurrency.CZK) {
            // this is from eur to czk
            return amount * 25.32;
        }
        // from czk to eur
        return amount * 0.039;
    }

    // todo API
//    public String currency() {
//        String url = "https://api.ratesapi.io/api/2010-01-12?base=EUR";
//        return this.restTemplate.getForObject(url, String.class);
//        if (response.getStatusCode() == HttpStatus.OK) {
//            return response.getBody();
//        } else {
//            return null;
//        }
//    }

    public Transaction updateBasic(int id, Transaction t) throws Exception {
        Transaction transaction = getByIdTransaction(id);
        BankAccount transBankAcc = transaction.getBankAccount();

        if (t.getAmount() < 0) {
            throw new NotValidDataException("Transaction amount is < 0");
        }

        updatedTransactionLogic(transaction, t, transBankAcc);

        transaction.setAmount(t.getAmount());
        transaction.setDate(t.getDate());
        transaction.setJottings(t.getJottings());
        return transactionDao.update(transaction);
    }

    public Transaction updateTransactionType(int id, TypeTransaction typeTransaction) throws Exception {
        Transaction transaction = getByIdTransaction(id);
        BankAccount transBankAcc = transaction.getBankAccount();

        updateTransactionTypeLogic(transaction, typeTransaction, transBankAcc);

        transaction.setTypeTransaction(typeTransaction);

        budgetLogicUpdateTypeTransaction(transaction);
        return transactionDao.update(transaction);
    }

    /**
     * Logic for update TransactionType
     * @param transaction
     * @throws Exception
     */
    private void budgetLogicUpdateTypeTransaction(Transaction transaction) throws Exception {
        if (transaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            budgetLogic(transaction.getBankAccount(), transaction);
        } else {
            Category transCategory = transaction.getCategory();
            double transAmount = transaction.getAmount();

            Budget budgetForTransaction = budgetDao.getByCategory(transCategory.getId(), transaction.getBankAccount().getId());
            if (budgetForTransaction == null) {
                return;
            }

            double sumAmount = budgetForTransaction.getSumAmount();

            budgetForTransaction.setSumAmount(sumAmount - transAmount);
            budgetDao.update(budgetForTransaction);

            double percentOfSumAmount = budgetForTransaction.getSumAmount() * 100 / budgetForTransaction.getAmount();
            checkNotifiesBudget(budgetForTransaction, percentOfSumAmount);
        }
    }

    private void updateTransactionTypeLogic(Transaction oldTransaction, TypeTransaction typeTransaction, BankAccount transBankAcc) {
        Double balance = transBankAcc.getBalance();
        if (oldTransaction.getTypeTransaction() != typeTransaction && typeTransaction == TypeTransaction.EXPENSE) {
            transBankAcc.setBalance(balance - oldTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        } else if (oldTransaction.getTypeTransaction() != typeTransaction && typeTransaction == TypeTransaction.INCOME) {
            transBankAcc.setBalance(balance + oldTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        }
    }

    private void updatedTransactionLogic(Transaction oldTransaction, Transaction updatedTransaction, BankAccount transBankAcc) throws Exception {
        Double balance = transBankAcc.getBalance();
        if (oldTransaction.getAmount() != updatedTransaction.getAmount() && oldTransaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            transBankAcc.setBalance(balance + oldTransaction.getAmount() - updatedTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
            budgetLogicUpdateTransactionAmount(oldTransaction, updatedTransaction);
        } else if (oldTransaction.getAmount() != updatedTransaction.getAmount() && oldTransaction.getTypeTransaction() == TypeTransaction.INCOME) {
            transBankAcc.setBalance(balance - oldTransaction.getAmount() + updatedTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        }
    }

    private void budgetLogicUpdateTransactionAmount(Transaction oldTransaction, Transaction updatedTransaction) throws Exception {
        BankAccount bankAccount = oldTransaction.getBankAccount();
        double oldTransAmount = oldTransaction.getAmount();
        double updatedTransaAmount = updatedTransaction.getAmount();

        Category category = oldTransaction.getCategory();
        Budget budgetForTransaction = budgetDao.getByCategory(category.getId(), bankAccount.getId());
        if (budgetForTransaction == null) {
            return;
        }

        double sumAmount = budgetForTransaction.getSumAmount();
        budgetForTransaction.setSumAmount(sumAmount - oldTransAmount + updatedTransaAmount);
        budgetDao.update(budgetForTransaction);
        double percentOfSumAmount = budgetForTransaction.getSumAmount() * 100 / budgetForTransaction.getAmount();
        // check if exists budgetNotify
        checkNotifiesBudget(budgetForTransaction, percentOfSumAmount);
    }

    public void remove(int id) throws Exception {
        Transaction transaction = getByIdTransaction(id);
        if (transaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            budgetLogicTransactionDelete(transaction);
        }
        transaction.setCategory(null);
        transaction.setBankAccount(null);
        transactionDao.remove(transaction);
    }

    public void removeFromCategory(int transId) throws Exception {
        Transaction t = getByIdTransaction(transId);
        t.setCategory(null);
        transactionDao.update(t);
    }

    public void budgetLogicTransactionDelete(Transaction transaction) throws Exception {
        BankAccount bankAccount = transaction.getBankAccount();
        double transAmount = transaction.getAmount();
        Category category = transaction.getCategory();
        Budget budgetForTransaction = budgetDao.getByCategory(category.getId(), bankAccount.getId());
        if (budgetForTransaction == null) {
            return;
        }

        double sumAmount = budgetForTransaction.getSumAmount();
        budgetForTransaction.setSumAmount(sumAmount - transAmount);
        budgetDao.update(budgetForTransaction);
        double percentOfSumAmount = budgetForTransaction.getSumAmount() * 100 / budgetForTransaction.getAmount();
        // check if exists budgetNotify
        checkNotifiesBudget(budgetForTransaction, percentOfSumAmount);
    }
}
