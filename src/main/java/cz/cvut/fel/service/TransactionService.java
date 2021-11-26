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

    /**
     * get Transactions sorted by month and year from bankAccount.
     *
     * @param month
     * @param year
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public List<Transaction> getSortedByMonthAndYear(int month, int year, int bankAccId) throws Exception {
        return transactionDao.getByMonthSortedAndYear(month, year, getByIdBankAccount(bankAccId).getId());
    }

    /**
     * get all Transactions sorted by month and year and type from bankAccount.
     *
     * @param accId
     * @param type
     * @param month
     * @param year
     * @return
     * @throws Exception
     */
    public List<Transaction> getTransactionsByType(int accId, TypeTransaction type, int month, int year) throws Exception {
        return transactionDao.getTransactionsByType(getByIdBankAccount(accId).getId(), type, month, year);
    }

    /**
     * get sum of expense Transactions between date from bankAccount.
     *
     * @param from
     * @param to
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public double getSumOfExpenseBetweenDate(Date from, Date to, int bankAccId) throws Exception {
        return transactionDao.getExpenseSum(from, to, getByIdBankAccount(bankAccId).getId());
    }

    /**
     * get sum of incomes Transactions between date from bankAccount.
     *
     * @param from
     * @param to
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public double getSumOfIncomeBetweenDate(Date from, Date to, int bankAccId) throws Exception {
        return transactionDao.getIncomeSum(from, to, getByIdBankAccount(bankAccId).getId());
    }

    /**
     * get sum of expense Transactions by category from bankAccount between date.
     *
     * @param from
     * @param to
     * @param bankAccId
     * @param catId
     * @return
     * @throws Exception
     */
    public double getSumOfExpenseWithCategory(Date from, Date to, int bankAccId, int catId)
            throws Exception {
        return transactionDao.getExpenseSumWithCategory(from, to, getByIdBankAccount(bankAccId).getId(), getByIdCategory(catId).getId());
    }

    /**
     * get sum of income Transactions by category from bankAccount.
     *
     * @param month
     * @param year
     * @param bankAccId
     * @param catId
     * @return
     * @throws Exception
     */
    public double getSumOfIncomeWithCategory(int month, int year, int bankAccId, int catId)
            throws Exception {
        return transactionDao.getIncomeSumWithCategory(month, year, getByIdBankAccount(bankAccId).getId(), getByIdCategory(catId).getId());
    }

    /**
     * get all Transactions by category from bankAccount.
     *
     * @param catId
     * @param accountId
     * @param month
     * @param year
     * @return
     * @throws Exception
     */
    public List<Transaction> getAllTransFromCategoryFromBankAcc(int catId, int accountId, int month, int year) throws
            Exception {
        return transactionDao.getAllTransFromCategory(getByIdCategory(catId).getId(), getByIdBankAccount(accountId).getId(), month, year);
    }

    /**
     * get all Transactions by type and category from bankAccount.
     *
     * @param catId
     * @param accountId
     * @param type
     * @param month
     * @param year
     * @return
     * @throws Exception
     */
    public List<Transaction> getTransactionsByTypeAndCategory(int catId, int accountId, TypeTransaction type, int month, int year) throws
            Exception {
        return transactionDao.getTransactionsByTypeAndCategory(getByIdCategory(catId).getId(), getByIdBankAccount(accountId).getId(), type, month, year);
    }

    /**
     * get transactions between date from BankAccount.
     *
     * @param from
     * @param to
     * @param accountId
     * @return
     * @throws Exception
     */
    public List<Transaction> getBetweenDate(Date from, Date to, int accountId) throws Exception {
        return transactionDao.getBetweenDate(from, to, getByIdBankAccount(accountId).getId());
    }

    /**
     * Returns all transactions from BankAccount that belong to a certain Category and are in a between date.
     *
     * @param categoryId - categoryId
     * @param accountId
     * @param from
     * @param to
     * @return
     * @throws Exception
     */
    public List<Transaction> getAllTransFromCategoryBetweenDate(int categoryId, int accountId, Date from, Date to) throws Exception {
        return transactionDao.getAllTransFromCategoryBetweenDate(getByIdCategory(categoryId).getId(),
                getByIdBankAccount(accountId).getId(), from, to);
    }

    /**
     * returns all transactions from BankAccount by category and type and are in a between date.
     *
     * @param categoryId
     * @param bankAccId
     * @param type
     * @param from
     * @param to
     * @return
     */
    public List<Transaction> getTransactionsByTypeAndCategoryBetweenDate(int categoryId, int bankAccId,
                                                                         TypeTransaction type, Date from, Date to) throws Exception {
        return transactionDao.getTransactionsByTypeAndCategoryBetweenDate(getByIdCategory(categoryId).getId(),
                getByIdBankAccount(bankAccId).getId(), type, from, to);
    }

    /**
     * returns all transactions from BankAccount by TypeTransaction and are in a between date.
     *
     * @param bankAccId
     * @param type
     * @param from
     * @param to
     * @return
     */
    public List<Transaction> getTransactionsByTypeBetweenDate(int bankAccId, TypeTransaction type, Date from, Date to) throws Exception {
        return transactionDao.getTransactionsByTypeBetweenDate(getByIdBankAccount(bankAccId).getId(), type, from, to);
    }

    /**
     * Persist transaction to BankAccount with Category.
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
        transaction.setBudget(null);
        Transaction persistedTransaction = transactionDao.persist(transaction);

        b.getTransactions().add(persistedTransaction);
        // bankAccount logic
        bankAccountLogic(b, persistedTransaction);
        bankAccountDao.update(b);
        return persistedTransaction;
    }

    /**
     * validation.
     *
     * @param transaction
     * @return
     */
    private boolean validate(Transaction transaction) {
        if (transaction.getTypeTransaction() == null || transaction.getAmount() <= 0) {
            return false;
        }
        return true;
    }

    /**
     * Logic for persist transaction to bankAccount.
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
     * Logic for Budget, when add an expense transaction with a Category that belongs to the Budget.
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

        budgetForTransaction.getTransactions().add(transaction);
        transaction.setBudget(budgetForTransaction);
        transactionDao.update(transaction);

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
     * Logic for Transfer transaction to another BankAccount.
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
        transferTransaction.setBudget(null);

        Transaction persistedTransaction = transactionDao.persist(transferTransaction);
        toBankAcc.getTransactions().add(transferTransaction);
        // logika pro toBankAccount pri pridani transakci
        bankAccountLogic(toBankAcc, transferTransaction);
        bankAccountDao.update(toBankAcc);

        // Odstrani transakci z BankAccount
        removeTransactionFromBankAccount(transaction.getId());
        return persistedTransaction;
    }

    /**
     * Currency convert.
     * From czk to eur and eur to czk.
     *
     * @param amount
     * @param currency
     * @return
     */
    private double currencyConvertLogic(double amount, TypeCurrency currency) {
        if (currency == TypeCurrency.CZK) {
            // this is from eur to czk
            return amount * 25.32;
        }
        // from czk to eur
        return amount * 0.039;
    }

    // todo API - not implemented yet, but will be needed in the future.
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
     * update only amount, date, jottings.
     * affects to BankAccount balance and budget.
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
     * update type of Transaction.
     * affects to the Budget and Bank Account.
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
     * update Category in Transaction.
     * It affects to the Budget entity.
     *
     * @param transactionId
     * @param categoryId
     * @return
     * @throws Exception
     */
    public Transaction updateCategoryTransaction(int transactionId, int categoryId) throws Exception {
        Transaction transaction = getByIdTransaction(transactionId);
        Category category = getByIdCategory(categoryId);
        if (transaction.getCategory() == category) {
            return null;
        }
        transaction.setCategory(category);
        category.getTransactions().add(transaction);
        categoryDao.update(category);
        Transaction updatedTransaction = transactionDao.update(transaction);
        if (transaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            if (transaction.getBudget() != null) {
                // budgetLogic pro old Budget pri update Category pro Expense Transaction
                budgetLogicTransactionUpdateCategory(transaction);
            } else {
                // budget logic for new budget
                budgetLogic(transaction.getBankAccount(), transaction);
            }
        }
        return updatedTransaction;
    }

    /**
     * Logic for update category in transaction.
     *
     * @param transaction
     * @throws Exception
     */
    private void budgetLogicTransactionUpdateCategory(Transaction transaction) throws Exception {
        BankAccount bankAccount = transaction.getBankAccount();
        double transAmount = transaction.getAmount();

        // get old budget
        Budget budgetForTransaction = transaction.getBudget();

        double sumAmount = budgetForTransaction.getSumAmount();
        budgetForTransaction.setSumAmount(sumAmount - transAmount);
        budgetDao.update(budgetForTransaction);

        double percentOfSumAmount = budgetForTransaction.getSumAmount() * 100 / budgetForTransaction.getAmount();

        checkNotifiesBudget(budgetForTransaction, percentOfSumAmount);

        budgetForTransaction.getTransactions().remove(transaction);
        budgetDao.update(budgetForTransaction);
        transaction.setBudget(null);
        transactionDao.update(transaction);
        // budget logic for new category
        budgetLogic(bankAccount, transaction);
    }

    /**
     * Logic for update TransactionType.
     *
     * @param transaction
     * @throws Exception
     */
    private void budgetLogicUpdateTypeTransaction(Transaction transaction) throws Exception {
        if (transaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            budgetLogic(transaction.getBankAccount(), transaction);
        } else {
            double transAmount = transaction.getAmount();

            Budget budgetForTransaction = transaction.getBudget();
            // check if Budget for Transaction exists
            if (budgetForTransaction == null) {
                return;
            }

            double sumAmount = budgetForTransaction.getSumAmount();
            budgetForTransaction.setSumAmount(sumAmount - transAmount);
            budgetForTransaction.getTransactions().remove(transaction);
            budgetDao.update(budgetForTransaction);

            double percentOfSumAmount = budgetForTransaction.getSumAmount() * 100 / budgetForTransaction.getAmount();
            checkNotifiesBudget(budgetForTransaction, percentOfSumAmount);

            transaction.setBudget(null);
            transactionDao.update(transaction);
        }
    }

    /**
     * Logic for update Type of Transaction.
     *
     * @param oldTransaction
     * @param typeTransaction
     * @param transBankAcc
     */
    private void updateTransactionTypeLogic(Transaction oldTransaction, TypeTransaction typeTransaction, BankAccount transBankAcc) {
        Double balance = transBankAcc.getBalance();
        if (oldTransaction.getTypeTransaction() != typeTransaction && typeTransaction == TypeTransaction.EXPENSE) {
            transBankAcc.setBalance(balance - oldTransaction.getAmount() - oldTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        } else if (oldTransaction.getTypeTransaction() != typeTransaction && typeTransaction == TypeTransaction.INCOME) {
            transBankAcc.setBalance(balance + oldTransaction.getAmount() + oldTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        }
    }

    /**
     * Logic for update amount in Transaction.
     * if an transaction of type Expense, then invokes budget logic
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
     * Budget Logic for update amount of expense transaction.
     *
     * @param oldTransaction
     * @param updatedTransaction
     * @throws Exception
     */
    private void budgetLogicUpdateTransactionAmount(Transaction oldTransaction, Transaction updatedTransaction) throws Exception {
        double oldTransAmount = oldTransaction.getAmount();
        double updatedTransAmount = updatedTransaction.getAmount();

        Budget budgetForTransaction = oldTransaction.getBudget();
        if (budgetForTransaction == null) {
            return;
        }

        double sumAmount = budgetForTransaction.getSumAmount();
        budgetForTransaction.setSumAmount(sumAmount - oldTransAmount + updatedTransAmount);
        budgetDao.update(budgetForTransaction);
        double percentOfSumAmount = budgetForTransaction.getSumAmount() * 100 / budgetForTransaction.getAmount();
        // check if exists budgetNotify
        checkNotifiesBudget(budgetForTransaction, percentOfSumAmount);
    }
}
