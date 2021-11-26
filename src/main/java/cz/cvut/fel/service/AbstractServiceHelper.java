package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.dto.TypeTransaction;
import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * This Class is used to share function between Service Classes.
 */
@Transactional
abstract class AbstractServiceHelper {
    protected final UserDao userDao;
    protected final BankAccountDao bankAccountDao;
    protected final TransactionDao transactionDao;
    protected final BudgetDao budgetDao;
    protected final DebtDao debtDao;
    protected final CategoryDao categoryDao;
    protected final NotifyBudgetDao notifyBudgetDao;
    protected final NotifyDebtDao notifyDebtDao;

    private static final Logger log = Logger.getLogger(AbstractServiceHelper.class.getName());

    public AbstractServiceHelper(UserDao userDao, BankAccountDao bankAccountDao, TransactionDao transactionDao,
                                 BudgetDao budgetDao, DebtDao debtDao, CategoryDao categoryDao,
                                 NotifyBudgetDao notifyBudgetDao, NotifyDebtDao notifyDebtDao) {
        this.userDao = userDao;
        this.bankAccountDao = bankAccountDao;
        this.transactionDao = transactionDao;
        this.budgetDao = budgetDao;
        this.debtDao = debtDao;
        this.categoryDao = categoryDao;
        this.notifyBudgetDao = notifyBudgetDao;
        this.notifyDebtDao = notifyDebtDao;
    }

    /**
     * return Authenticated user.
     *
     * @return
     * @throws NotAuthenticatedClient
     */
    public User getAuthenticatedUser() throws NotAuthenticatedClient {
        try {
            User user = userDao.find(SecurityUtils.getCurrentUser().getId());
            if (user == null) {
                throw new UserNotFoundException();
            }
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new NotAuthenticatedClient();
        }
    }

    /**
     * get user By Id.
     *
     * @param id
     * @return
     * @throws UserNotFoundException
     */
    public User getByIdUser(int id) throws UserNotFoundException {
        User u = userDao.find(id);
        if (u == null) {
            throw new UserNotFoundException(id);
        }
        return u;
    }

    /**
     * get User by username.
     *
     * @param username
     * @return
     */
    public User getByUsername(String username) {
        return userDao.getByUsername(username);
    }

    /**
     * get all default categories, which have id < 0, and are located in import.sql resource file.
     *
     * @return
     */
    public List<Category> getDefaultCategories() {
        return categoryDao.getDefaultCategories();
    }

    /**
     * get by Id category.
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Category getByIdCategory(int id) throws Exception {
        Category c = categoryDao.find(id);
        if (c == null) {
            throw new CategoryNotFoundException(id);
        }
        if (!isCreatorOfCategory(c.getId())) {
            throw new NotAuthenticatedClient();
        }
        return c;
    }

    /**
     * get by id Debt.
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Debt getByIdDebt(int id) throws Exception {
        Debt d = debtDao.find(id);
        if (d == null) {
            throw new DebtNotFoundException(id);
        }
        if (!isOwnerOfDebt(d)) {
            throw new NotAuthenticatedClient();
        }
        return d;
    }

    /**
     * get by id Budget.
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Budget getByIdBudget(int id) throws Exception {
        Budget budget = budgetDao.find(id);
        if (budget == null) {
            throw new BudgetNotFoundException(id);
        }
        if (!isOwnerOfBudget(budget)) {
            throw new NotAuthenticatedClient();
        }
        return budget;
    }

    /**
     * check if user has the right to access the Budget.
     * I will find out if the user is the owner of the Bank Account to which the Budget belongs
     *
     * @param budget
     * @return
     * @throws Exception
     */
    public boolean isOwnerOfBudget(Budget budget) throws Exception {
        BankAccount budgetsBankAccount = budget.getBankAccount();

        return isUserOwnerOrCreatorOfBankAccount(budgetsBankAccount);
    }

    /**
     * check if the user has the right to access Debt.
     * I will find out if the user is the owner of the Bank Account to which the Debt belongs
     *
     * @param debt
     * @return
     * @throws Exception
     */
    public boolean isOwnerOfDebt(Debt debt) throws Exception {
        BankAccount debtBankAccount = debt.getBankAccount();

        return isUserOwnerOrCreatorOfBankAccount(debtBankAccount);
    }

    /**
     * get Transaction by id.
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Transaction getByIdTransaction(int id) throws Exception {
        Transaction t = transactionDao.find(id);
        if (t == null) {
            throw new TransactionNotFoundException(id);
        }
        if (!isUserOwnerOrCreatorOfBankAccount(t.getBankAccount())) {
            throw new NotAuthenticatedClient();
        }
        return t;
    }

    /**
     * Check if the user is creator of category.
     *
     * @param categoryId
     * @return
     * @throws Exception
     */
    public boolean isCreatorOfCategory(int categoryId) throws Exception {
        User user = getAuthenticatedUser();
        return categoryDao.getUsersCategoryById(user.getId(), categoryId) != null;
    }

    /**
     * get BankAccount by Id
     *
     * @param id
     * @return
     * @throws Exception
     */
    public BankAccount getByIdBankAccount(int id) throws Exception {
        BankAccount bankAccount = bankAccountDao.find(id);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException(id);
        }
        if (!isUserOwnerOrCreatorOfBankAccount(bankAccount)) {
            throw new NotAuthenticatedClient();
        }
        return bankAccount;
    }

    /**
     * check if the user is among the owners of bankAccount or is a creator.
     *
     * @param bankAccount
     * @return
     * @throws Exception
     */
    public boolean isUserOwnerOrCreatorOfBankAccount(BankAccount bankAccount) throws Exception {
        User user = getAuthenticatedUser();
        if (bankAccount.getCreator() == user) {
            return true;
        }
        return bankAccountDao.getUsersAvailableBankAccountById(user.getId(), bankAccount.getId()) != null;
    }

    /**
     * check if the user is among the owners of bankAccount.
     *
     * @param user
     * @param bankAccount
     * @return
     * @throws Exception
     */
    public boolean isUserOwnerOfBankAccount(User user, BankAccount bankAccount) throws Exception {
        return bankAccountDao.getUsersAvailableBankAccountById(user.getId(), bankAccount.getId()) != null;
    }

    /**
     * Create NotifyBudget with TypeNotification.
     *
     * @param budgetForTransaction
     * @param typeNotification
     * @throws Exception
     */
    public void createNotifyBudget(Budget budgetForTransaction, TypeNotification typeNotification) {
        if (notifyBudgetDao.alreadyExistsBudget(budgetForTransaction.getId(), typeNotification)) {
            return;
        }
        NotifyBudget notifyBudgetEntity = new NotifyBudget();
        notifyBudgetEntity.setBudget(budgetForTransaction);
        notifyBudgetEntity.setTypeNotification(typeNotification);
        notifyBudgetDao.persist(notifyBudgetEntity);
    }

    /**
     * Remove Transaction from BankAccount.
     *
     * @param transactionId
     * @throws Exception
     */
    public void removeTransactionFromBankAccount(int transactionId) throws Exception {
        removeTransaction(transactionId);
    }

    /**
     * Delete Transaction from db.
     * Remove transaction from budget
     * Update bankAccount balance
     *
     * @param id
     * @throws Exception
     */
    public void removeTransaction(int id) throws Exception {
        Transaction transaction = getByIdTransaction(id);
        BankAccount bankAccount = getByIdBankAccount(transaction.getBankAccount().getId());

        if (transaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            // budget logic pri delete Expense
            budgetLogicTransactionDelete(transaction);
        }

        bankAccount.getTransactions().remove(transaction);
        Double actualBalance = bankAccount.getBalance();
        if (transaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            bankAccount.setBalance(actualBalance + transaction.getAmount());
        } else {
            bankAccount.setBalance(actualBalance - transaction.getAmount());
        }
        bankAccountDao.update(bankAccount);

        transaction.setCategory(null);
        transaction.setBankAccount(null);
        transactionDao.remove(transaction);
    }

    /**
     * Budget logic for delete Expense transaction.
     *
     * @param transaction
     * @throws Exception
     */
    private void budgetLogicTransactionDelete(Transaction transaction) throws Exception {
        double transAmount = transaction.getAmount();

        Budget budgetForTransaction = transaction.getBudget();
        if (budgetForTransaction == null) {
            return;
        }

        double sumAmount = budgetForTransaction.getSumAmount();
        budgetForTransaction.setSumAmount(sumAmount - transAmount);
        budgetForTransaction.getTransactions().remove(transaction);
        budgetDao.update(budgetForTransaction);
        double percentOfSumAmount = budgetForTransaction.getSumAmount() * 100 / budgetForTransaction.getAmount();
        // check if exists budgetNotify
        checkNotifiesBudget(budgetForTransaction, percentOfSumAmount);

        transaction.setBudget(null);
        transactionDao.update(transaction);
    }

    /**
     * check if the budget sumAmount and percentNotify has changed to not notify status.
     *
     * @param actualBudget
     * @param percentOfSumAmount - actual percent of sumAmount from budget.amount
     * @throws Exception
     */
    public void checkNotifiesBudget(Budget actualBudget, double percentOfSumAmount) throws Exception {
        NotifyBudget notifyBudgetAmount = notifyBudgetDao.getBudgetsNotifyBudgetByType(actualBudget.getId(), TypeNotification.BUDGET_AMOUNT);
        if (actualBudget.getSumAmount() < actualBudget.getAmount()) {
            if (notifyBudgetAmount != null) {
                notifyBudgetDao.deleteNotifyBudgetById(notifyBudgetAmount.getId());
            }
        } else {
            if (notifyBudgetAmount == null) {
                createNotifyBudget(actualBudget, TypeNotification.BUDGET_AMOUNT);
            }
        }
        NotifyBudget notifyBudgetPercent = notifyBudgetDao.getBudgetsNotifyBudgetByType(actualBudget.getId(), TypeNotification.BUDGET_PERCENT);
        if (percentOfSumAmount < actualBudget.getPercentNotify()) {
            if (notifyBudgetPercent != null) {
                notifyBudgetDao.deleteNotifyBudgetById(notifyBudgetPercent.getId());
            }
        } else {
            if (notifyBudgetPercent == null) {
                createNotifyBudget(actualBudget, TypeNotification.BUDGET_PERCENT);
            }
        }
    }

    /**
     * check if Transaction in BankAccount.
     *
     * @param bankAccountId
     * @param transactionId
     * @return
     */
    public boolean isTransactionInBankAcc(int bankAccountId, int transactionId) {
        return transactionDao.getFromBankAcc(bankAccountId, transactionId) != null;
    }

    /**
     * Delete Budget from db.
     *
     * @param budgetId
     * @throws Exception
     */
    public void removeBudget(int budgetId) throws Exception {
        Budget budget = getByIdBudget(budgetId);

        for (Transaction transaction : budget.getTransactions()) {
            transaction.setBudget(null);
        }

        budget.setTransactions(Collections.emptyList());
        budget.setBankAccount(null);
        budgetDao.update(budget);

        removeNotifyBudgetByBudgetId(budget.getId());
        budgetDao.deleteAllBudgetRelationWithCategoryById(budget.getId());
        budgetDao.remove(budget);
    }

    /**
     * Delete notifyBudget by Budget Id, if not exists then do nothing.
     *
     * @param budgetId
     * @throws Exception
     */
    private void removeNotifyBudgetByBudgetId(int budgetId) throws Exception {
        // get notifyBudget by budgetId test if exists
        if (notifyBudgetDao.getNotifyBudgetByBudgetId(budgetId).isEmpty()) {
            return;
        }
        notifyBudgetDao.deleteNotifyBudgetByBudgetId(budgetId);
    }

    /**
     * Delete Debt from db.
     *
     * @param debtId
     * @throws Exception
     */
    public void removeDebt(int debtId) throws Exception {
        Debt debt = getByIdDebt(debtId);
        removeNotifyDebtByDebtId(debtId);
        debtDao.remove(debt);
    }

    /**
     * Delete notifyDebt by Debt Id, if not exists then do nothing.
     *
     * @param debtId
     * @throws Exception
     */
    private void removeNotifyDebtByDebtId(int debtId) throws Exception {
        // get notifyDebt by debtId test if exists
        if (notifyDebtDao.getNotifyDebtByDebtId(debtId).isEmpty()) {
            return;
        }
        notifyDebtDao.deleteNotifyDebtByDebtId(debtId);
    }

    /**
     * Delete bankAccount from db.
     * Only Creator of BankAccount can delete the BankAccount.
     *
     * @param bankAccId
     * @throws Exception
     */
    public void removeBankAcc(int bankAccId) throws Exception {
        User user = getAuthenticatedUser();
        BankAccount bankAccount = getByIdBankAccount(bankAccId);

        // pokud user neni creator throw Exception
        if (bankAccount.getCreator() != user) {
            throw new NotAuthenticatedClient();
        }

        for (User owner : bankAccount.getOwners()) {
            bankAccountDao.deleteRelationBankAcc(owner.getId(), bankAccount.getId());
        }

        bankAccount.setCreator(null);
        // delete entity from db
        bankAccountDao.remove(bankAccount);
    }


    /**
     * Delete category from db.
     * After deleting a category, set the default category for all its transactions to "No category".
     * All budgets from the DB for which the category has been removed will also be removed
     * It is forbidden to delete default categories - they have ids from -1 to -15
     *
     * @param categoryId
     * @throws Exception
     */
    public void removeCategory(int categoryId) throws Exception {
        if (categoryId < 0) {
            throw new Exception("Deleting a category is prohibited ");
        }

        Category noCategory = getByIdCategory(-12);
        Category category = getByIdCategory(categoryId);

        for (Transaction transaction : category.getTransactions()) {
            // "No category" entity
            transaction.setCategory(noCategory);
        }

        // odstraneni Budgets z DB
        for (Budget budget : category.getBudget()) {
            removeBudget(budget.getId());
        }

        category.getTransactions().clear();
        category.setBudget(null);
        category.setCreators(null);
        // delete relation
        categoryDao.deleteUsersRelationCategoryById(getAuthenticatedUser().getId(), categoryId);
        //delete entity from db
        categoryDao.remove(category);
    }
}
