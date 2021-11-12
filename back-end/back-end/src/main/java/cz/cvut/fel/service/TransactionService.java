package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeCurrency;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.dto.TypeTransaction;
import cz.cvut.fel.model.*;
import cz.cvut.fel.service.exceptions.NotValidDataException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
@Transactional
public class TransactionService extends AbstractServiceHelper {

    private static final Logger log = Logger.getLogger(TransactionService.class.getName());

    public TransactionService(UserDao userDao, BankAccountDao bankAccountDao, TransactionDao transactionDao,
                              BudgetDao budgetDao, DebtDao debtDao, CategoryDao categoryDao,
                              NotifyBudgetDao notifyBudgetDao, NotifyDebtDao notifyDebtDao) {
        super(userDao, bankAccountDao, transactionDao, budgetDao, debtDao, categoryDao, notifyBudgetDao, notifyDebtDao);
    }

    public List<Transaction> getSortedByMonthAndYear(int month, int year, int bankAccId) throws Exception {
        return transactionDao.getByMonthSortedAndYear(month, year, getByIdBankAccount(bankAccId).getId());
    }

    public List<Transaction> getTransactionsByType(int accId, TypeTransaction type, int month, int year) throws Exception {
        return transactionDao.getTransactionsByType(getByIdBankAccount(accId).getId(), type, month, year);
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

    public List<Transaction> getAllTransFromCategoryFromBankAcc(int catId, int accountId, int month, int year) throws
            Exception {
        return transactionDao.getAllTransFromCategory(getByIdCategory(catId).getId(), getByIdBankAccount(accountId).getId(), month, year);
    }

    public List<Transaction> getTransactionsByTypeAndCategory(int catId, int accountId, TypeTransaction type, int month, int year) throws
            Exception {
        return transactionDao.getTransactionsByTypeAndCategory(getByIdCategory(catId).getId(), getByIdBankAccount(accountId).getId(), type, month, year);
    }

    public List<Transaction> getBetweenDate(String strFrom, String strTo, int accountId) throws Exception {
        return transactionDao.getBetweenDate(strFrom, strTo, getByIdBankAccount(accountId).getId());
    }

    /**
     * Persist transaction to BankAccount with Category
     *
     * @param transaction
     * @param bankAccId
     * @param categoryId
     * @return
     * @throws Exception
     */
    public Transaction persist(Transaction transaction, int bankAccId, int categoryId) throws Exception {
        Objects.requireNonNull(transaction);
        if (!validate(transaction))
            throw new NotValidDataException("transaction");

        BankAccount b = getByIdBankAccount(bankAccId);
        Category category = getByIdCategory(categoryId);
        transaction.setBankAccount(b);
        transaction.setCategory(category);
        transaction.setDate(transaction.getDate());
        Transaction persistedTransaction = transactionDao.persist(transaction);

        b.getTransactions().add(transaction);
        // bankAccount logic
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

    /**
     * Logika pri pridani transakce do BankAccount
     *
     * @param bankAccount
     * @param transaction
     * @throws Exception
     */
    private void bankAccountLogic(BankAccount bankAccount, Transaction transaction) throws Exception {
        if (transaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            bankAccount.setBalance(bankAccount.getBalance() - transaction.getAmount());
            budgetLogic(bankAccount, transaction);
        }
        if (transaction.getTypeTransaction() == TypeTransaction.INCOME) {
            bankAccount.setBalance(bankAccount.getBalance() + transaction.getAmount());
        }
    }

    /**
     * Logika pro Budget, kdyz se pridat transakce typu Expense s Category ktera patri do Budget
     *
     * @param bankAccount
     * @param transaction
     * @throws Exception
     */
    private void budgetLogic(BankAccount bankAccount, Transaction transaction) throws Exception {
        Category transCategory = transaction.getCategory();
        double transAmount = transaction.getAmount();

        Budget budgetForTransaction = budgetDao.getByCategory(transCategory.getId(), bankAccount.getId());
        if (budgetForTransaction == null) {
            return;
        }

        double sumAmount = budgetForTransaction.getSumAmount();

        budgetForTransaction.setSumAmount(sumAmount + transAmount);
        budgetDao.update(budgetForTransaction);

        double percentOfSumAmount = budgetForTransaction.getSumAmount() * 100 / budgetForTransaction.getAmount();
        if (budgetForTransaction.getSumAmount() >= budgetForTransaction.getAmount()) {
            createNotifyBudget(budgetForTransaction, TypeNotification.BUDGET_AMOUNT);
            log.info("BUDGET added to NotifyBudget with AmountType" + budgetForTransaction.getName());
        }

        if (percentOfSumAmount >= budgetForTransaction.getPercentNotify()) {
            createNotifyBudget(budgetForTransaction, TypeNotification.BUDGET_PERCENT);
            log.info("BUDGET added to NotifyBudget with PercentType " + budgetForTransaction.getName());
        }
    }

    /**
     * Logic for Transfer transaction to another BankAccount
     *
     * @param fromBankAccId - from bankAccount
     * @param toBankAccId   - to BankAccount
     * @param transactionId - Transaction id
     * @return
     * @throws Exception
     */
    public Transaction transferTransaction(int fromBankAccId, int toBankAccId, int transactionId) throws Exception {
        Transaction transaction = getByIdTransaction(transactionId);
        Transaction transferTransaction = new Transaction();

        double actualCurrencyAmount = transaction.getAmount();
        BankAccount toBankAcc = getByIdBankAccount(toBankAccId);
        BankAccount fromBankAcc = getByIdBankAccount(fromBankAccId);

        //cehck if Tranascke je v BankAcc
        if (!isTransactionInBankAcc(fromBankAccId, transactionId)) {
            throw new NotValidDataException("Transaction is not in From BankAccount");
        }

        transferTransaction.setBankAccount(toBankAcc);
        transferTransaction.setCategory(transaction.getCategory());
        if (transaction.getJottings().isEmpty()) {
            transferTransaction.setJottings("Transfer transaction from " + fromBankAcc.getName());
        } else {
            transferTransaction.setJottings(transaction.getJottings());
        }

        if (toBankAcc.getCurrency() != fromBankAcc.getCurrency()) {
            //Konverze meny
            actualCurrencyAmount = currencyConvertLogic(transaction.getAmount(), toBankAcc.getCurrency());
        }
        transferTransaction.setAmount(actualCurrencyAmount);
        transferTransaction.setDate(new Date());
        transferTransaction.setTypeTransaction(transaction.getTypeTransaction());

        Transaction persistedTransaction = transactionDao.persist(transferTransaction);
        toBankAcc.getTransactions().add(transferTransaction);
        // logika pro toBankAccount pri pridani transakci
        bankAccountLogic(toBankAcc, transferTransaction);
        bankAccountDao.update(toBankAcc);

        // Odstrani transakci z BankAccount
        removeTransactionFromBankAccount(transaction.getId(), fromBankAccId);
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

    // todo API - zatim neni implementovano, ale bude potreba v budoucnu
//    public String currency() {
//        String url = "https://api.ratesapi.io/api/2010-01-12?base=EUR";
//        return this.restTemplate.getForObject(url, String.class);
//        if (response.getStatusCode() == HttpStatus.OK) {
//            return response.getBody();
//        } else {
//            return null;
//        }
//    }

    /**
     * update only amount, date, jottings
     *
     * @param transactionId
     * @param updateTransaction
     * @return
     * @throws Exception
     */
    public Transaction updateBasic(int transactionId, Transaction updateTransaction) throws Exception {
        Transaction transaction = getByIdTransaction(transactionId);
        BankAccount transBankAcc = transaction.getBankAccount();

        if (updateTransaction.getAmount() < 0) {
            throw new NotValidDataException("Transaction amount is < 0");
        }

        // logic for update Amount
        updatedBasicTransactionLogic(transaction, updateTransaction, transBankAcc);

        transaction.setAmount(updateTransaction.getAmount());
        transaction.setDate(updateTransaction.getDate());
        transaction.setJottings(updateTransaction.getJottings());
        return transactionDao.update(transaction);
    }

    /**
     * update type of Transaction
     *
     * @param transactionId
     * @param typeTransaction
     * @return
     * @throws Exception
     */
    public Transaction updateTransactionType(int transactionId, TypeTransaction typeTransaction) throws Exception {
        Transaction transaction = getByIdTransaction(transactionId);
        BankAccount transBankAcc = transaction.getBankAccount();

        // logika editaci typu
        updateTransactionTypeLogic(transaction, typeTransaction, transBankAcc);

        transaction.setTypeTransaction(typeTransaction);
        // budget logic pri update Type
        budgetLogicUpdateTypeTransaction(transaction);
        return transactionDao.update(transaction);
    }

    /**
     * Editace kategorie u Transaction
     *
     * @param transactionId
     * @param categoryId
     * @return
     * @throws Exception
     */
    public Transaction updateCategoryTransaction(int transactionId, int categoryId) throws Exception {
        Transaction transaction = getByIdTransaction(transactionId);
        Category oldCategory = transaction.getCategory();
        Category category = getByIdCategory(categoryId);
        if (transaction.getCategory() == category) {
            return null;
        }
        transaction.setCategory(category);
        category.getTransactions().add(transaction);
        categoryDao.update(category);
        Transaction updatedTransaction = transactionDao.update(transaction);
        if (transaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            // budgetLogic pri update Category pro Expense Transaction
            budgetLogicTransactionUpdateCategory(oldCategory, transaction);
        }
        return updatedTransaction;
    }

    /**
     * Logika pri editaci Category u Transaction
     *
     * @param oldCategory
     * @param transaction
     * @throws Exception
     */
    private void budgetLogicTransactionUpdateCategory(Category oldCategory, Transaction transaction) throws Exception {
        BankAccount bankAccount = transaction.getBankAccount();
        double transAmount = transaction.getAmount();
        Budget budgetForTransaction = budgetDao.getByCategory(oldCategory.getId(), bankAccount.getId());
        if (budgetForTransaction == null) {
            return;
        }

        double sumAmount = budgetForTransaction.getSumAmount();
        budgetForTransaction.setSumAmount(sumAmount - transAmount);
        budgetDao.update(budgetForTransaction);

        double percentOfSumAmount = budgetForTransaction.getSumAmount() * 100 / budgetForTransaction.getAmount();

        checkNotifiesBudget(budgetForTransaction, percentOfSumAmount);

        budgetLogic(bankAccount, transaction);
    }

    /**
     * kontroluju pokud se zmenil stav budgetu, pokud se zmenil tak odstranim notifyBudget
     *
     * @param actualBudget
     * @param percentOfSumAmount - actual percent of sumAmount from budget.amount
     * @throws Exception
     */
    private void checkNotifiesBudget(Budget actualBudget, double percentOfSumAmount) throws Exception {
        if (actualBudget.getSumAmount() < actualBudget.getAmount()) {
            NotifyBudget notifyBudget = notifyBudgetDao.getBudgetsNotifyBudgetByType(actualBudget.getId(), TypeNotification.BUDGET_AMOUNT);
            if (notifyBudget != null) {
                notifyBudgetDao.deleteNotifyBudgetById(notifyBudget.getId());
            }
        }
        if (percentOfSumAmount < actualBudget.getPercentNotify()) {
            NotifyBudget notifyBudget = notifyBudgetDao.getBudgetsNotifyBudgetByType(actualBudget.getId(), TypeNotification.BUDGET_PERCENT);
            if (notifyBudget != null) {
                notifyBudgetDao.deleteNotifyBudgetById(notifyBudget.getId());
            }
        }
    }

    /**
     * Logic for update TransactionType
     *
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
            // check if Budget for Transaction exists
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

    /**
     * Logika pri editaci Type of Transaction
     *
     * @param oldTransaction
     * @param typeTransaction
     * @param transBankAcc
     */
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

    /**
     * Logika pri editaci amount transakce
     * pokud tranksakce typu Expense, tehdy se udela budget logic
     *
     * @param oldTransaction
     * @param updatedTransaction
     * @param transBankAcc
     * @throws Exception
     */
    private void updatedBasicTransactionLogic(Transaction oldTransaction, Transaction updatedTransaction, BankAccount transBankAcc) throws Exception {
        Double balance = transBankAcc.getBalance();
        if (oldTransaction.getAmount() != updatedTransaction.getAmount() && oldTransaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            transBankAcc.setBalance(balance + oldTransaction.getAmount() - updatedTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
            // budget logic
            budgetLogicUpdateTransactionAmount(oldTransaction, updatedTransaction);
        } else if (oldTransaction.getAmount() != updatedTransaction.getAmount() && oldTransaction.getTypeTransaction() == TypeTransaction.INCOME) {
            transBankAcc.setBalance(balance - oldTransaction.getAmount() + updatedTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        }
    }

    /**
     * Logika Budget pri update amount Expense transakci
     *
     * @param oldTransaction
     * @param updatedTransaction
     * @throws Exception
     */
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

    /**
     * Delete Transaction
     *
     * @param id
     * @throws Exception
     */
    public void remove(int id) throws Exception {
        Transaction transaction = getByIdTransaction(id);
        if (transaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            // budget logic pri delete Expense
            budgetLogicTransactionDelete(transaction);
        }
        transaction.setCategory(null);
        transaction.setBankAccount(null);
        transactionDao.remove(transaction);
    }

    /**
     * Budget logic pri delete Expense transaction
     *
     * @param transaction
     * @throws Exception
     */
    private void budgetLogicTransactionDelete(Transaction transaction) throws Exception {
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
