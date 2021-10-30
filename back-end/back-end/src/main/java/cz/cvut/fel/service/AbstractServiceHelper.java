package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeTransaction;
import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.*;

import java.util.List;

abstract class AbstractServiceHelper {
    protected UserDao userDao;
    protected BankAccountDao bankAccountDao;
    protected TransactionDao transactionDao;
    protected BudgetDao budgetDao;
    protected DebtDao debtDao;
    protected CategoryDao categoryDao;
    protected NotifyBudgetDao notifyBudgetDao;
    protected NotifyDebtDao notifyDebtDao;

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

    public Debt getByIdDebt(int id) throws DebtNotFoundException, NotAuthenticatedClient {
        Debt d = debtDao.find(id);
        if (d == null) {
            throw new DebtNotFoundException(id);
        }
        if (!isCreatorOfDebt(d)) {
            throw new NotAuthenticatedClient();
        }
        return d;
    }

    public Budget getByIdBudget(int id) throws BudgetNotFoundException, NotAuthenticatedClient {
        Budget budget = budgetDao.find(id);
        if (budget == null) {
            throw new BudgetNotFoundException(id);
        }
        if (!isOwnerOfBudget(budget)) {
            throw new NotAuthenticatedClient();
        }
        return budget;
    }

    public boolean isOwnerOfBudget(Budget budget) throws NotAuthenticatedClient {
        User user = getAuthenticatedUser();
        User creator = budget.getCreator();
        return creator.getId() == user.getId();
    }

    public boolean isCreatorOfDebt(Debt debt) throws NotAuthenticatedClient {
        User user = getAuthenticatedUser();
        return debt.getCreator().getId() == user.getId();
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
        return bankAccountDao.getUsersBankAccountById(user.getId(), bankAccount.getId()) != null;
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
        deleteNotifyBudgetByBudgetId(bu.getId());
        budgetDao.remove(bu);
    }

    /**
     * Delete notifyBudget by Budget Id, if not exists then do nothing
     *
     * @param budgetId
     * @throws Exception
     */
    private void deleteNotifyBudgetByBudgetId(int budgetId) throws Exception {
        int uid = getAuthenticatedUser().getId();
        // get notifyBudget by budgetId test if exists
        if (notifyBudgetDao.getNotifyBudgetByBudgetId(uid, budgetId).isEmpty()) {
            return;
        }
        notifyBudgetDao.deleteNotifyBudgetByBudgetId(uid, budgetId);
    }

    public void removeDebt(int id) throws Exception {
        Debt debt = getByIdDebt(id);
        removeNotifyDebtByDebtId(id);
        debtDao.remove(debt);
    }

    /**
     * Delete notifydebt by Debt Id, if not exists then do nothing
     * @param debtId
     * @throws Exception
     */
    private void removeNotifyDebtByDebtId(int debtId) throws Exception {
        int uid = getAuthenticatedUser().getId();
        // get notifyDebt by debtId test if exists
        if (notifyDebtDao.getNotifyDebtByDebtId(uid, debtId).isEmpty()) {
            return;
        }
        notifyDebtDao.deleteNotifyDebtByDebtId(uid, debtId);
    }

    /**
     * Nejdrive smazu relation a potom samotnou entitu
     *
     * @param id
     * @throws Exception
     */
    public void removeBankAcc(int id) throws Exception {
        BankAccount bankAccount = getByIdBankAccount(id);
        bankAccount.getOwners().clear();
        bankAccount.getBudgets().clear();
        bankAccount.getDebts().clear();
        // delete relation
        bankAccountDao.deleteRelationBankAcc(getAuthenticatedUser().getId(), id);
        // delete entity from db
        bankAccountDao.remove(bankAccount);
    }

}
