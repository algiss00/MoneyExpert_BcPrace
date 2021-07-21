package cz.cvut.fel.service;

import cz.cvut.fel.dao.BankAccountDao;
import cz.cvut.fel.dao.BudgetDao;
import cz.cvut.fel.dao.CategoryDao;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.Category;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

@Service
@Transactional
public class BudgetService {
    private BudgetDao budgetDao;
    private BankAccountService bankAccountService;
    private BankAccountDao bankAccountDao;
    private UserService userService;
    private CategoryService categoryService;
    private CategoryDao categoryDao;

    @Autowired
    public BudgetService(BudgetDao budgetDao, BankAccountService bankAccountService, BankAccountDao bankAccountDao,
                         UserService userService, CategoryService categoryService, CategoryDao categoryDao) {
        this.budgetDao = budgetDao;
        this.bankAccountService = bankAccountService;
        this.bankAccountDao = bankAccountDao;
        this.userService = userService;
        this.categoryService = categoryService;
        this.categoryDao = categoryDao;
    }

    public List<Budget> getAll() {
        return budgetDao.findAll();
    }

    public List<Budget> getAllAccountsBudgets(int accId) throws BankAccountNotFoundException {
        bankAccountService.getById(accId);
        return budgetDao.getAllAccountsBudgets(accId);
    }

    public List<Budget> getAllUsersBudgets(int uid) throws UserNotFoundException {
        userService.getById(uid);
        return budgetDao.getAllUsersBudgets(uid);
    }

    public Budget getById(int id) throws BudgetNotFoundException {
        Budget budget = budgetDao.find(id);
        if (budget == null) {
            throw new BudgetNotFoundException(id);
        }
        return budget;
    }

    public boolean persist(Budget budget, int uid, int accId, int categoryId) throws UserNotFoundException,
            BankAccountNotFoundException, CategoryNotFoundException {
        if (budget == null)
            throw new NullPointerException("budget can not be Null.");
        if (!validate(budget))
            return false;
        Category category = categoryService.getById(categoryId);
        User u = userService.getById(uid);
        BankAccount bankAccount = bankAccountService.getById(accId);
        budget.setCreator(u);
        budget.setCategory(category);
        budget.setBankAccount(bankAccount);
        budgetDao.persist(budget);
        bankAccount.getBudgets().add(budget);
        bankAccountDao.update(bankAccount);
        category.setBudget(budget);
        categoryDao.update(category);
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
        User u = userService.getById(uid);
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

    public void removeBudgetFromBankAcc(int budgetId, int accId) throws BankAccountNotFoundException, BudgetNotFoundException {
        Budget budget = getById(budgetId);
        BankAccount bankAccount = bankAccountService.getById(accId);

        bankAccount.getBudgets().remove(budget);
        budget.setBankAccount(null);
        budgetDao.update(budget);
        bankAccountDao.update(bankAccount);
    }

    public void removeBudgetFromCategory(int buId) throws BudgetNotFoundException {
        Budget budget = getById(buId);
        budget.setCategory(null);
        budgetDao.update(budget);
    }

}
