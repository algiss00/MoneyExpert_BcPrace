package cz.cvut.fel.service;

import cz.cvut.fel.dao.BankAccountDao;
import cz.cvut.fel.dao.BudgetDao;
import cz.cvut.fel.dao.CategoryDao;
import cz.cvut.fel.dao.UserDao;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.Category;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class BudgetService {
    private BudgetDao budgetDao;
    private BankAccountDao bankAccountDao;
    private UserDao userDao;
    private CategoryDao categoryDao;

    @Autowired
    public BudgetService(BudgetDao budgetDao, BankAccountDao bankAccountDao,
                         UserDao userDao, CategoryDao categoryDao) {
        this.budgetDao = budgetDao;
        this.bankAccountDao = bankAccountDao;
        this.userDao = userDao;
        this.categoryDao = categoryDao;
    }

    public List<Budget> getAll() {
        return budgetDao.findAll();
    }

    public Budget getById(int id) throws BudgetNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        Budget budget = budgetDao.find(id);
        if (budget == null) {
            throw new BudgetNotFoundException(id);
        }
        if (!isOwnerOfBudget(budget)) {
            throw new NotAuthenticatedClient();
        }
        return budget;
    }

    public boolean persist(Budget budget, int accId, int categoryId) throws UserNotFoundException,
            BankAccountNotFoundException, CategoryNotFoundException, NotAuthenticatedClient {
        Objects.requireNonNull(budget);
        User u = isLogged();
        Category category = categoryDao.find(categoryId);
        if (category == null)
            throw new CategoryNotFoundException();
        BankAccount bankAccount = bankAccountDao.find(accId);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException();
        }
        if (!isOwnOfBankAcc(bankAccount) || !isOwnCategory(category)) {
            throw new NotAuthenticatedClient();
        }
        if (!validate(budget, bankAccount))
            return false;
        budget.setCreator(u);
        budget.setCategory(category);
        budget.setBankAccount(bankAccount);
        budget.setSumAmount(0);
        budgetDao.persist(budget);
        bankAccount.getBudgets().add(budget);
        bankAccountDao.update(bankAccount);
        return true;
    }

    private boolean isOwnCategory(Category category) throws UserNotFoundException, NotAuthenticatedClient {
        User user = isLogged();
        List<User> creators = category.getCreators();
        for (User creator : creators) {
            if (creator.getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }

    private boolean isOwnOfBankAcc(BankAccount bankAccount) throws UserNotFoundException, NotAuthenticatedClient {
        User user = isLogged();
        List<User> owners = bankAccount.getOwners();
        for (User owner : owners) {
            if (owner.getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }

    private boolean validate(Budget budget, BankAccount bankAccount) {
        if (budgetDao.find(budget.getId()) != null) {
            return false;
        }
        return !budget.getName().trim().isEmpty() && budget.getAmount() > 0
                && !isBudgetCategoryExist(budget.getCategory(), bankAccount.getBudgets());
    }

    private boolean isBudgetCategoryExist(Category budCategory, List<Budget> bankAccBudgets) {
        for (Budget budget : bankAccBudgets) {
            if (budget.getCategory() == budCategory) {
                return true;
            }
        }
        return false;
    }


    public boolean remove(int id) throws UserNotFoundException, BudgetNotFoundException, NotAuthenticatedClient {
        Budget bu = getById(id);
        budgetDao.remove(bu);
        return true;
    }

    public Budget updateBudget(int id, Budget budget) throws BudgetNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        Budget b = getById(id);
        b.setName(budget.getName());
        //todo logic of amount
        b.setAmount(budget.getAmount());
        b.setPercentNotif(budget.getPercentNotif());
        b.setCategory(budget.getCategory());

        return budgetDao.update(b);
    }

    public void removeCategoryFromBudget(int buId) throws BudgetNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        Budget budget = getById(buId);
        budget.setCategory(null);
        budgetDao.update(budget);
    }

    private boolean isOwnerOfBudget(Budget budget) throws UserNotFoundException, NotAuthenticatedClient {
        User user = isLogged();
        User creator = budget.getCreator();
        return creator.getId() == user.getId();
    }

    private User isLogged() throws NotAuthenticatedClient, UserNotFoundException {
        if (SecurityUtils.getCurrentUser() == null) {
            throw new NotAuthenticatedClient();
        }
        User user = userDao.find(SecurityUtils.getCurrentUser().getId());
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }
}
