package cz.cvut.fel.service;

import cz.cvut.fel.dao.BankAccountDao;
import cz.cvut.fel.dao.BudgetDao;
import cz.cvut.fel.dao.UserDao;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.CategoryEnum;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BudgetService {
    private BudgetDao budgetDao;
    private BankAccountDao bankAccountDao;
    private UserDao userDao;

    @Autowired
    public BudgetService(BudgetDao budgetDao, BankAccountDao bankAccountDao,
                         UserDao userDao) {
        this.budgetDao = budgetDao;
        this.bankAccountDao = bankAccountDao;
        this.userDao = userDao;
    }

    public List<Budget> getAll() {
        return budgetDao.findAll();
    }

    public List<Budget> getAllAccountsBudgets(int accId) throws BankAccountNotFoundException {
        if (bankAccountDao.find(accId) == null) {
            throw new BankAccountNotFoundException();
        }
        return budgetDao.getAllAccountsBudgets(accId);
    }

    public List<Budget> getAllUsersBudgets(int uid) throws UserNotFoundException {
        if (userDao.find(uid) == null)
            throw new UserNotFoundException();
        return budgetDao.getAllUsersBudgets(uid);
    }

    public Budget getById(int id) throws BudgetNotFoundException {
        Budget budget = budgetDao.find(id);
        if (budget == null) {
            throw new BudgetNotFoundException(id);
        }
        return budget;
    }

    public boolean persist(Budget budget, int uid, int accId, CategoryEnum category) throws UserNotFoundException,
            BankAccountNotFoundException, CategoryNotFoundException {
        if (budget == null)
            throw new NullPointerException("budget can not be Null.");
        if (!validate(budget))
            return false;
        User u = userDao.find(uid);
        if (u == null)
            throw new UserNotFoundException();
        BankAccount bankAccount = bankAccountDao.find(accId);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException();
        }
        budget.setCreator(u);
        budget.setCategory(category);
        budget.setBankAccount(bankAccount);
        budgetDao.persist(budget);
        bankAccount.getBudgets().add(budget);
        bankAccountDao.update(bankAccount);
        return true;
    }

    public boolean validate(Budget budget) {
        return !budget.getName().trim().isEmpty();
    }


    public boolean remove(int id, int userId) throws UserNotFoundException, BudgetNotFoundException, NotAuthenticatedClient {
        if (SecurityUtils.getCurrentUser() == null) {
            throw new NotAuthenticatedClient();
        }
        Budget bu = getById(id);
        if (!alreadyExists(bu.getId()) || !isOwner(userId, id))
            return false;
//        bu.setCreator(null);
//        bu.setCategory(null);
//        bu.setBankAccount(null);
//        budgetDao.update(bu);
        budgetDao.remove(bu);
        return true;
    }

    public boolean isOwner(int uid, int bid) throws UserNotFoundException {
        User u = userDao.find(uid);
        if (u == null) {
            throw new UserNotFoundException(uid);
        }
        for (Budget budget : u.getMyBudgets()) {
            if (budget.getId() == bid) {
                return true;
            }
        }
        return false;
    }

    public boolean alreadyExists(int budgetId) {
        return budgetDao.find(budgetId) != null;
    }

    public Budget updateBudget(int id, Budget budget) throws BudgetNotFoundException {
        Budget b = getById(id);
        b.setName(budget.getName());
        b.setAmount(budget.getAmount());
        b.setPercentNotif(budget.getPercentNotif());
        b.setCategory(budget.getCategory());

        return budgetDao.update(b);
    }

    public void removeBudgetFromCategory(int buId) throws BudgetNotFoundException {
        Budget budget = getById(buId);
        budget.setCategory(null);
        budgetDao.update(budget);
    }

}
