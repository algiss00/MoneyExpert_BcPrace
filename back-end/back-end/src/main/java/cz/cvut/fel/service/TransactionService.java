package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.dto.TypeTransaction;
import cz.cvut.fel.model.*;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
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

    public List<Transaction> getSortedByMonth(int month, int bankAccId) throws Exception {
        return transactionDao.getByMonthSorted(month, getByIdBankAccount(bankAccId).getId());
    }

    public double getSumOfExpenseOnMonth(int month, int bankAccId) throws Exception {
        return transactionDao.getExpenseSum(month, getByIdBankAccount(bankAccId).getId());
    }

    public double getSumOfIncomeOnMonth(int month, int bankAccId) throws Exception {
        return transactionDao.getIncomeSum(month, getByIdBankAccount(bankAccId).getId());
    }

    public double getSumOfExpenseWithCategory(int month, int bankAccId, int catId)
            throws Exception {
        return transactionDao.getExpenseSumWithCategory(month, getByIdBankAccount(bankAccId).getId(), getByIdCategory(catId).getId());
    }

    public double getSumOfIncomeWithCategory(int month, int bankAccId, int catId)
            throws Exception {
        return transactionDao.getIncomeSumWithCategory(month, getByIdBankAccount(bankAccId).getId(), getByIdCategory(catId).getId());
    }

    public List<Transaction> getAllTransFromCategoryFromBankAcc(int catId, int accountId) throws
            Exception {
        return transactionDao.getAllTransFromCategory(getByIdCategory(catId).getId(), getByIdBankAccount(accountId).getId());
    }

    public List<Transaction> getBetweenDate(String strFrom, String strTo, int accountId) throws Exception {
        return transactionDao.getBetweenDate(strFrom, strTo, getByIdBankAccount(accountId));
    }

    public boolean persist(Transaction transaction, int accId, int categoryId) throws Exception {
        Objects.requireNonNull(transaction);
        if (!validate(transaction))
            return false;

        BankAccount b = getByIdBankAccount(accId);
        Category category = getByIdCategory(categoryId);
        transaction.setBankAccount(b);
        transaction.setCategory(category);
        transaction.setDate(new Date());
        transactionDao.persist(transaction);

        b.getTransactions().add(transaction);
        bankAccountLogic(b, transaction);
        bankAccountDao.update(b);
        return true;
    }

    private void budgetLogic(BankAccount bankAccount, Transaction transaction) throws Exception {
        Category transCategory = transaction.getCategory();
        double transAmount = transaction.getAmount();

        Budget budgetForTransaction = budgetDao.getByCategory(transCategory.getId(), bankAccount.getId());
        if (budgetForTransaction == null) {
            System.out.println("NO BUDGET FOR THIS CATEGORY");
            return;
        }

        budgetForTransaction.setSumAmount(budgetForTransaction.getSumAmount() + transAmount);
        budgetDao.update(budgetForTransaction);
        double percentOfSumAmount = budgetForTransaction.getSumAmount() * 100 / budgetForTransaction.getAmount();

        if (budgetForTransaction.getSumAmount() >= budgetForTransaction.getAmount()) {
            System.out.println("trans amount is bigger or equal then budget amount NOTIFICATE");
            if (notifyBudgetDao.alreadyExistsBudget(budgetForTransaction.getId(), TypeNotification.BUDGET_AMOUNT)) {
                System.out.println("EXISTS");
                return;
            }
            NotifyBudget notifyBudgetEntity = new NotifyBudget();
            notifyBudgetEntity.setCreator(budgetForTransaction.getCreator());
            notifyBudgetEntity.setBudget(budgetForTransaction);
            notifyBudgetEntity.setTypeNotification(TypeNotification.BUDGET_AMOUNT);

            notifyBudgetDao.persist(notifyBudgetEntity);
            System.out.println("ADDED TO BUDGET AMOUNT " + notifyBudgetEntity.getBudget().getName());
        } else if (percentOfSumAmount >= budgetForTransaction.getPercentNotif()) {
            System.out.println("PROCENT NOTIFICATE");
            if (notifyBudgetDao.alreadyExistsBudget(budgetForTransaction.getId(), TypeNotification.BUDGET_PERCENT)) {
                System.out.println("EXISTS");
                return;
            }
            NotifyBudget notifyBudgetEntity = new NotifyBudget();
            notifyBudgetEntity.setCreator(budgetForTransaction.getCreator());
            notifyBudgetEntity.setBudget(budgetForTransaction);
            notifyBudgetEntity.setTypeNotification(TypeNotification.BUDGET_PERCENT);

            notifyBudgetDao.persist(notifyBudgetEntity);
            System.out.println("ADDED TO BUDGET PERCENT " + notifyBudgetEntity.getBudget().getName());
        }
    }

    private boolean validate(Transaction t) {
        if (transactionDao.find(t.getId()) != null) {
            return false;
        }
        return !(t.getAmount() <= 0);
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

        BankAccount toBankAcc = getByIdBankAccount(toAccId);
        BankAccount fromBankAcc = getByIdBankAccount(fromAccId);

        transferTransaction.setBankAccount(toBankAcc);
        transferTransaction.setCategory(transaction.getCategory());
        if (transaction.getJottings().isEmpty()) {
            transferTransaction.setJottings("Transfer transaction from " + fromBankAcc.getName());
        } else {
            transferTransaction.setJottings(transaction.getJottings());
        }
        transferTransaction.setAmount(transaction.getAmount());
        transferTransaction.setDate(new Date());
        transferTransaction.setTypeTransaction(TypeTransaction.INCOME);

        transactionDao.persist(transferTransaction);
        bankAccountLogic(toBankAcc, transferTransaction);

        transaction.setTypeTransaction(TypeTransaction.EXPENSE);
        bankAccountLogic(fromBankAcc, transaction);

        toBankAcc.getTransactions().add(transferTransaction);
        bankAccountDao.update(toBankAcc);
        return transaction;
    }

    // todo
//    public MonetaryAmount convertFromCZKtoEUR(double czkAmount) {
//        MonetaryAmount czk = Monetary.getDefaultAmountFactory().setCurrency("CZK")
//                .setNumber(czkAmount).create();
//
//        CurrencyConversion conversionEUR = MonetaryConversions.getConversion("EUR");
//
//        MonetaryAmount convertedAmountUSDtoEUR = czk.with(conversionEUR);
//        return convertedAmountUSDtoEUR;
//    }

    public Transaction update(int id, Transaction t) throws Exception {
        Transaction transaction = getByIdTransaction(id);
        BankAccount transBankAcc = transaction.getBankAccount();

        updatedTransactionLogic(transaction, t, transBankAcc);

        transaction.setAmount(t.getAmount());
        transaction.setDate(t.getDate());
        transaction.setJottings(t.getJottings());
        return transactionDao.update(transaction);
    }

    public Transaction updateCategory(int tid, int catId) throws Exception {
        Transaction transaction = getByIdTransaction(tid);
        Category category = getByIdCategory(catId);

        transaction.setCategory(category);
        return transactionDao.update(transaction);
    }

    public Transaction updateTransactionType(int id, TypeTransaction typeTransaction) throws Exception {
        Transaction transaction = getByIdTransaction(id);
        BankAccount transBankAcc = transaction.getBankAccount();

        updateTransactionTypeLogic(transaction, typeTransaction, transBankAcc);

        transaction.setTypeTransaction(typeTransaction);
        return transactionDao.update(transaction);
    }

    private void updateTransactionTypeLogic(Transaction oldTransaction, TypeTransaction typeTransaction, BankAccount transBankAcc) {
        double balance = transBankAcc.getBalance();
        if (oldTransaction.getTypeTransaction() != typeTransaction && typeTransaction == TypeTransaction.EXPENSE) {
            transBankAcc.setBalance(balance - oldTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        } else if (oldTransaction.getTypeTransaction() != typeTransaction && typeTransaction == TypeTransaction.INCOME) {
            transBankAcc.setBalance(balance + oldTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        }
    }

    private void updatedTransactionLogic(Transaction oldTransaction, Transaction updatedTransaction, BankAccount transBankAcc) {
        double balance = transBankAcc.getBalance();
        if (oldTransaction.getAmount() != updatedTransaction.getAmount() && oldTransaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            transBankAcc.setBalance(balance + oldTransaction.getAmount() - updatedTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        } else if (oldTransaction.getAmount() != updatedTransaction.getAmount() && oldTransaction.getTypeTransaction() == TypeTransaction.INCOME) {
            transBankAcc.setBalance(balance - oldTransaction.getAmount() + updatedTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        }
    }

    public void remove(int id) throws Exception {
        Transaction transaction = getByIdTransaction(id);
        transaction.setCategory(null);
        transaction.setBankAccount(null);
        transactionDao.remove(transaction);
    }

    public void removeFromCategory(int transId) throws Exception {
        Transaction t = getByIdTransaction(transId);
        t.setCategory(null);
        transactionDao.update(t);
    }
}
