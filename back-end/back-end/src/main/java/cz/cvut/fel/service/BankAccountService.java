package cz.cvut.fel.service;

import cz.cvut.fel.dao.BankAccountDao;
import cz.cvut.fel.dao.BudgetDao;
import cz.cvut.fel.dao.UserDao;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.BankAccountNotFoundException;
import cz.cvut.fel.service.exceptions.BudgetNotFoundException;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BankAccountService {
    private BankAccountDao bankAccountDao;
    private UserService userService;
    private UserDao userDao;
    private BudgetDao budgetDao;

    @Autowired
    public BankAccountService(BankAccountDao bankAccountDao, UserService userService, UserDao userDao, BudgetDao budgetDao) {
        this.bankAccountDao = bankAccountDao;
        this.userService = userService;
        this.userDao = userDao;
        this.budgetDao = budgetDao;
    }

    public List<BankAccount> getAll() {
        return bankAccountDao.findAll();
    }

    public List<BankAccount> getAllUsersAccounts(int id) throws UserNotFoundException {
        User u = userService.getById(id);
        return u.getAvailableBankAccounts();
    }

    public BankAccount getById(int id) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountDao.find(id);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException(id);
        }
        return bankAccount;
    }

    public List<User> getAllOwners(int accId) throws BankAccountNotFoundException {
        BankAccount b = getById(accId);
        return b.getOwners();
    }

    public boolean persist(BankAccount bankAccount, int userId) throws UserNotFoundException {
        if (bankAccount == null)
            throw new NullPointerException("bankAccount cannot be Null.");
        if (!validate(bankAccount))
            return false;
        User u = userService.getById(userId);
        bankAccount.getOwners().add(u);
        bankAccountDao.persist(bankAccount);
        u.getAvailableBankAccounts().add(bankAccount);
        userDao.update(u);
        return true;
    }

    public void addNewOwner(int userId, int accId) throws UserNotFoundException, BankAccountNotFoundException {
        User u = userService.getById(userId);
        BankAccount b = getById(accId);

        b.getOwners().add(u);
        u.getAvailableBankAccounts().add(b);
        bankAccountDao.update(b);
        userDao.update(u);
    }

    public boolean validate(BankAccount bankAccount) {
        return !bankAccount.getName().trim().isEmpty() && !alreadyExists(bankAccount);
    }

    public boolean alreadyExists(BankAccount bankAccount) {
        return bankAccountDao.find(bankAccount.getId()) != null;
    }


    public BankAccount updateAccount(int id, BankAccount bankAccount) throws BankAccountNotFoundException {
        BankAccount b = getById(id);
        b.setName(bankAccount.getName());
        b.setCurrency(bankAccount.getCurrency());
        return bankAccountDao.update(b);
    }

    // TODO: 30.05.2021 Pridej Prava, kontrolu pro delete
    public void remove(int id) throws BankAccountNotFoundException, NotAuthenticatedClient, BudgetNotFoundException {
        if (SecurityUtils.getCurrentUser() == null) {
            throw new NotAuthenticatedClient();
        }
        BankAccount bankAccount = getById(id);
        removeBudgetsFromBankAcc(bankAccount);
//        bankAccount.getBudgets().clear();
//        bankAccountDao.update(bankAccount);
//        bankAccount.getOwners().clear();
//        bankAccountDao.update(bankAccount);
//        bankAccount.getDebts().clear();
//        bankAccountDao.update(bankAccount);

        bankAccountDao.remove(bankAccount);
    }

    public void removeOwner(int userId, int accId) throws UserNotFoundException, BankAccountNotFoundException {
        User u = userService.getById(userId);
        BankAccount b = getById(accId);

        b.getOwners().remove(u);
        u.getAvailableBankAccounts().remove(b);
        bankAccountDao.update(b);
        userDao.update(u);
    }

    public void removeBudgetsFromBankAcc(BankAccount bankAccount) throws BankAccountNotFoundException, BudgetNotFoundException {
        for (Budget budget : bankAccount.getBudgets()) {
            bankAccount.getBudgets().remove(budget);
            budget.setBankAccount(null);
            budgetDao.update(budget);
            bankAccountDao.update(bankAccount);
        }
    }

}
