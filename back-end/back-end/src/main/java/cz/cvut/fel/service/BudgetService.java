package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.Category;
import cz.cvut.fel.model.User;
import cz.cvut.fel.service.exceptions.*;
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
    private UserService userService;
    private CategoryService categoryService;
    private BankAccountService bankAccountService;

    public BudgetService(CategoryDao categoryDao, UserDao userDao, TransactionDao transactionDao,
                         BankAccountDao bankAccountDao, BudgetDao budgetDao, NotifyBudgetDao notifyBudgetDao, DebtDao debtDao) {
        this.budgetDao = budgetDao;
        this.bankAccountDao = bankAccountDao;
        this.userDao = userDao;
        this.categoryDao = categoryDao;
        this.userService = new UserService(userDao);
        this.categoryService = new CategoryService(categoryDao, userDao, transactionDao, bankAccountDao, budgetDao, notifyBudgetDao, debtDao);
        this.bankAccountService = new BankAccountService(categoryDao, bankAccountDao, userDao, transactionDao, budgetDao, debtDao, notifyBudgetDao);
    }

    public List<Budget> getAll() {
        return budgetDao.findAll();
    }

    public Budget getById(int id) throws BudgetNotFoundException, NotAuthenticatedClient {
        Budget budget = budgetDao.find(id);
        if (budget == null) {
            throw new BudgetNotFoundException(id);
        }
        if (!isOwnerOfBudget(budget)) {
            throw new NotAuthenticatedClient();
        }
        return budget;
    }

    public boolean persist(Budget budget, int accId, int categoryId) throws
            BankAccountNotFoundException, CategoryNotFoundException, NotAuthenticatedClient {
        Objects.requireNonNull(budget);
        User u = userService.isLogged();
        Category category = categoryService.getById(categoryId);

        BankAccount bankAccount = bankAccountService.getById(accId);
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

    private boolean validate(Budget budget, BankAccount bankAccount) {
        if (budgetDao.find(budget.getId()) != null) {
            return false;
        }
        return !budget.getName().trim().isEmpty() && budget.getAmount() > 0
                && !isBudgetCategoryExist(budget.getCategory(), bankAccount.getBudgets());
    }

    private boolean isBudgetCategoryExist(Category budCategory, List<Budget> bankAccBudgets) {
        //todo sql
        for (Budget budget : bankAccBudgets) {
            if (budget.getCategory() == budCategory) {
                return true;
            }
        }
        return false;
    }

    public boolean remove(int id) throws BudgetNotFoundException, NotAuthenticatedClient {
        Budget bu = getById(id);
        budgetDao.remove(bu);
        return true;
    }

    public Budget updateBudget(int id, Budget budget) throws BudgetNotFoundException, NotAuthenticatedClient {
        Budget b = getById(id);
        b.setName(budget.getName());
        //todo logic of amount
        b.setAmount(budget.getAmount());
        b.setPercentNotif(budget.getPercentNotif());
        b.setCategory(budget.getCategory());

        return budgetDao.update(b);
    }

    public void removeCategoryFromBudget(int buId) throws BudgetNotFoundException, NotAuthenticatedClient {
        Budget budget = getById(buId);
        budget.setCategory(null);
        budgetDao.update(budget);
    }

    private boolean isOwnerOfBudget(Budget budget) throws NotAuthenticatedClient {
        User user = userService.isLogged();
        User creator = budget.getCreator();
        return creator.getId() == user.getId();
    }
}
