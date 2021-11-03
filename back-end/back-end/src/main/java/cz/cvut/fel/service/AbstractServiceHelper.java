package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.dto.TypeTransaction;
import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.*;

import java.util.List;
import java.util.logging.Logger;

abstract class AbstractServiceHelper {
    protected UserDao userDao;
    protected BankAccountDao bankAccountDao;
    protected TransactionDao transactionDao;
    protected BudgetDao budgetDao;
    protected DebtDao debtDao;
    protected CategoryDao categoryDao;
    protected NotifyBudgetDao notifyBudgetDao;
    protected NotifyDebtDao notifyDebtDao;

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

    public User getByIdUser(int id) throws UserNotFoundException {
        User u = userDao.find(id);
        if (u == null) {
            throw new UserNotFoundException(id);
        }
        return u;
    }

    public User getByUsername(String username) {
        return userDao.getByUsername(username);
    }

    public List<Category> getDefaultCategories() {
        return categoryDao.getDefaultCategories();
    }

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

    public boolean isOwnerOfBudget(Budget budget) throws Exception {
        BankAccount budgetsBankAccount = budget.getBankAccount();

        return isUserOwnerOfBankAccount(budgetsBankAccount);
    }

    public boolean isOwnerOfDebt(Debt debt) throws Exception {
        BankAccount debtBankAccount = debt.getBankAccount();

        return isUserOwnerOfBankAccount(debtBankAccount);
    }

    public Transaction getByIdTransaction(int id) throws Exception {
        Transaction t = transactionDao.find(id);
        if (t == null) {
            throw new TransactionNotFoundException(id);
        }
        if (!isUserOwnerOfBankAccount(t.getBankAccount())) {
            throw new NotAuthenticatedClient();
        }
        return t;
    }

    public boolean isCreatorOfCategory(int categoryId) throws Exception {
        User user = getAuthenticatedUser();
        return categoryDao.getUsersCategoryById(user.getId(), categoryId) != null;
    }

    public BankAccount getByIdBankAccount(int id) throws Exception {
        BankAccount bankAccount = bankAccountDao.find(id);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException(id);
        }
        if (!isUserOwnerOfBankAccount(bankAccount)) {
            throw new NotAuthenticatedClient();
        }
        return bankAccount;
    }

    public boolean isUserOwnerOfBankAccount(BankAccount bankAccount) throws Exception {
        User user = getAuthenticatedUser();
        if (bankAccount.getCreator() == user) {
            return true;
        }
        return bankAccountDao.getUsersAvailableBankAccountById(user.getId(), bankAccount.getId()) != null;
    }

    public boolean isUserOwnerOfBankAccount(User user, BankAccount bankAccount) throws Exception {
        return bankAccountDao.getUsersAvailableBankAccountById(user.getId(), bankAccount.getId()) != null;
    }

    public Transaction updateCategoryTransaction(int tid, int catId) throws Exception {
        Transaction transaction = getByIdTransaction(tid);
        Category oldCategory = transaction.getCategory();
        Category category = getByIdCategory(catId);
        if (transaction.getCategory() == category) {
            return null;
        }
        transaction.setCategory(category);
        category.getTransactions().add(transaction);
        categoryDao.update(category);
        Transaction updatedTransaction = transactionDao.update(transaction);
        if (transaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            budgetLogicTransactionUpdateCategory(oldCategory, transaction);
        }
        return updatedTransaction;
    }

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
    public void checkNotifiesBudget(Budget actualBudget, double percentOfSumAmount) throws Exception {
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

    public void budgetLogic(BankAccount bankAccount, Transaction transaction) throws Exception {
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


    public void createNotifyBudget(Budget budgetForTransaction, TypeNotification typeNotification) throws Exception {
        if (notifyBudgetDao.alreadyExistsBudget(budgetForTransaction.getId(), typeNotification)) {
            return;
        }
        NotifyBudget notifyBudgetEntity = new NotifyBudget();
        notifyBudgetEntity.setBudget(budgetForTransaction);
        notifyBudgetEntity.setTypeNotification(typeNotification);
        notifyBudgetDao.persist(notifyBudgetEntity);
    }

    public void removeTransFromAccount(int transId, int accId) throws Exception {
        BankAccount bankAccount = getByIdBankAccount(accId);
        Transaction transaction = getByIdTransaction(transId);
        if (!isTransactionInBankAcc(bankAccount.getId(), transaction.getId())) {
            throw new NotAuthenticatedClient();
        }
        bankAccount.getTransactions().remove(transaction);
        Double actualBalance = bankAccount.getBalance();
        if (transaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            bankAccount.setBalance(actualBalance + transaction.getAmount());
        } else {
            bankAccount.setBalance(actualBalance - transaction.getAmount());
        }
        transaction.setBankAccount(null);
        bankAccountDao.update(bankAccount);
        transactionDao.update(transaction);
        transactionDao.remove(transaction);
    }

    public boolean isTransactionInBankAcc(int bankAccountId, int transactionId) {
        return transactionDao.getFromBankAcc(bankAccountId, transactionId) != null;
    }

    public void removeBudget(int id) throws Exception {
        Budget bu = getByIdBudget(id);
        removeNotifyBudgetByBudgetId(bu.getId());
        budgetDao.deleteAllBudgetRelationWithCategoryById(bu.getId());
        budgetDao.deleteBudgetById(bu.getId());
    }

    /**
     * Delete notifyBudget by Budget Id, if not exists then do nothing
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

    public void removeDebt(int id) throws Exception {
        Debt debt = getByIdDebt(id);
        removeNotifyDebtByDebtId(id);
        debtDao.remove(debt);
    }

    /**
     * Delete notifydebt by Debt Id, if not exists then do nothing
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
     * Only Creator of BankAccount can delete the BankAccount
     *
     * @param id
     * @throws Exception
     */
    public void removeBankAcc(int id) throws Exception {
        User user = getAuthenticatedUser();
        BankAccount bankAccount = getByIdBankAccount(id);

        // pokud user neni creator throw Exception
        if (bankAccount.getCreator() != user) {
            throw new NotAuthenticatedClient();
        }

        for (Debt debt : bankAccount.getDebts()) {
            removeDebt(debt.getId());
        }

        for (Budget budget : bankAccount.getBudgets()) {
            removeBudget(budget.getId());
        }

        for (User owner : bankAccount.getOwners()) {
            bankAccountDao.deleteRelationBankAcc(owner.getId(), bankAccount.getId());
        }

        bankAccount.setCreator(null);
        // delete entity from db
        bankAccountDao.remove(bankAccount);
    }


    /**
     * Pri smazani category nastavim defualt category u vsech jeji transakci na "No category"
     * Je zakazno delete default categories - maji id od -1 do -12
     *
     * @param id
     * @throws Exception
     */
    public void removeCategory(int id) throws Exception {
        if (id < 0) {
            throw new Exception("Deleting a category is prohibited ");
        }

        Category noCategory = getByIdCategory(-12);
        Category category = getByIdCategory(id);
        category.getTransactions().forEach(transaction -> {
            try {
                // "No category" entity
                transaction.setCategory(noCategory);
                transactionDao.update(transaction);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        for (Budget budget : category.getBudget()) {
            removeBudget(budget.getId());
        }

        category.getTransactions().clear();
        category.setBudget(null);
        category.setCreators(null);
        // delete relation
        categoryDao.deleteUsersRelationCategoryById(getAuthenticatedUser().getId(), id);
        //delete entity from db
        categoryDao.remove(category);
    }
}
