package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.*;

import java.util.List;

abstract class AbstractServiceHelper {
    UserDao userDao;
    BankAccountDao bankAccountDao;
    TransactionDao transactionDao;
    BudgetDao budgetDao;
    DebtDao debtDao;
    CategoryDao categoryDao;
    NotifyBudgetDao notifyBudgetDao;
    NotifyDebtDao notifyDebtDao;

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
        User user;
        try {
            user = userDao.find(SecurityUtils.getCurrentUser().getId());
            if (user == null) {
                throw new UserNotFoundException();
            }
        } catch (Exception ex) {
            throw new NotAuthenticatedClient();
        }
        return user;
    }

    public User getByIdUser(String id) throws UserNotFoundException {
        User u = userDao.find(id);
        if (u == null) {
            throw new UserNotFoundException(id);
        }
        return u;
    }

    public Category getByIdCategory(String id) throws CategoryNotFoundException, NotAuthenticatedClient {
        Category c = categoryDao.find(id);
        if (c == null) {
            throw new CategoryNotFoundException(id);
        }
        //todo for default categories
        if (!isCreatorOfCategory(c)) {
            throw new NotAuthenticatedClient();
        }
        return c;
    }

    public Debt getByIdDebt(String id) throws DebtNotFoundException, NotAuthenticatedClient {
        Debt d = debtDao.find(id);
        if (d == null) {
            throw new DebtNotFoundException(id);
        }
        if (!isCreatorOfDebt(d)) {
            throw new NotAuthenticatedClient();
        }
        return d;
    }

    public Budget getByIdBudget(String id) throws BudgetNotFoundException, NotAuthenticatedClient {
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
        return creator.getId().equals(user.getId());
    }

    public boolean isCreatorOfDebt(Debt debt) throws NotAuthenticatedClient {
        User user = isLogged();
        return debt.getCreator().getId().equals(user.getId());
    }

    public Transaction getByIdTransaction(String id) throws TransactionNotFoundException, NotAuthenticatedClient {
        Transaction t = transactionDao.find(id);
        if (t == null) {
            throw new TransactionNotFoundException(id);
        }
        if (!isOwnerOfTransaction(t)) {
            throw new NotAuthenticatedClient();
        }
        return t;
    }

    public boolean isCreatorOfCategory(Category category) throws NotAuthenticatedClient {
        User user = isLogged();
        List<User> creators = category.getCreators();
        // todo sql
        for (User creator : creators) {
            if (creator.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public BankAccount getByIdBankAccount(String id) throws BankAccountNotFoundException, NotAuthenticatedClient {
        BankAccount bankAccount = bankAccountDao.find(id);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException(id);
        }
        if (!isUserOwnerOfBankAccount(bankAccount)) {
            throw new NotAuthenticatedClient();
        }
        return bankAccount;
    }

    public boolean isUserOwnerOfBankAccount(BankAccount bankAccount) throws NotAuthenticatedClient {
        User user = isLogged();
        List<User> owners = bankAccount.getOwners();
        // todo sql
        for (User owner : owners) {
            if (owner.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isOwnerOfTransaction(Transaction t) throws NotAuthenticatedClient {
        User user = isLogged();
        List<BankAccount> bankAccounts = user.getAvailableBankAccounts();
        // todo sql
        for (BankAccount bankAccount : bankAccounts) {
            if (bankAccount.getId().equals(t.getBankAccount().getId())) {
                return true;
            }
        }
        return false;
    }
}
