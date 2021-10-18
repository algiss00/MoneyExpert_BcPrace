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

    public User isLogged() throws NotAuthenticatedClient {
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
        User user = isLogged();
        User creator = budget.getCreator();
        return creator.getId() == user.getId();
    }

    public boolean isCreatorOfDebt(Debt debt) throws NotAuthenticatedClient {
        User user = isLogged();
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
        User user = isLogged();
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
        User user = isLogged();
        return bankAccountDao.getUsersBankAccountById(user.getId(), bankAccount.getId()) != null;
    }

    public void removeTransFromAccount(int transId, int accId) throws Exception {
        BankAccount bankAccount = getByIdBankAccount(accId);
        Transaction transaction = getByIdTransaction(transId);
        if (!isTransactionInBankAcc(bankAccount.getId(), transaction.getId())) {
            throw new NotAuthenticatedClient();
        }
        bankAccount.getTransactions().remove(transaction);
        double actualBalance = bankAccount.getBalance();
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
}
