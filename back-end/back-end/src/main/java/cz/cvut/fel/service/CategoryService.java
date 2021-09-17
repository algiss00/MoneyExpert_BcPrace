package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
import cz.cvut.fel.service.exceptions.CategoryNotFoundException;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.TransactionNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class CategoryService {
    private CategoryDao categoryDao;
    private UserDao userDao;
    private TransactionDao transactionDao;
    private UserService userService;
    private TransactionService transactionService;

    public CategoryService(CategoryDao categoryDao, UserDao userDao, TransactionDao transactionDao,
                           BankAccountDao bankAccountDao, BudgetDao budgetDao, NotifyBudgetDao notifyBudgetDao, DebtDao debtDao) {
        this.categoryDao = categoryDao;
        this.userDao = userDao;
        this.transactionDao = transactionDao;
        this.userService = new UserService(userDao);
        this.transactionService = new TransactionService(transactionDao, bankAccountDao, userDao,
                categoryDao, budgetDao, notifyBudgetDao, debtDao);
    }

    public List<Category> getAll() {
        return categoryDao.findAll();
    }

    public Budget getBudget(int catId) throws CategoryNotFoundException, NotAuthenticatedClient {
        return getById(catId).getBudget();
    }

    public List<Transaction> getTransactions(int catId) throws CategoryNotFoundException, NotAuthenticatedClient {
        return getById(catId).getTransactions();
    }

    public Category getById(int id) throws CategoryNotFoundException, NotAuthenticatedClient {
        Category c = categoryDao.find(id);
        if (c == null) {
            throw new CategoryNotFoundException(id);
        }
        //todo for default categories
        if (!isCreator(c)) {
            throw new NotAuthenticatedClient();
        }
        return c;
    }

    public boolean persist(Category category) throws NotAuthenticatedClient {
        Objects.requireNonNull(category);
        if (!validate(category))
            return false;
        User u = userService.isLogged();

        category.getCreators().add(u);
        categoryDao.persist(category);
        u.getMyCategories().add(category);
        userDao.update(u);
        return true;
    }

    public void addTransactionToCategory(int transId, int categoryId) throws TransactionNotFoundException, CategoryNotFoundException, NotAuthenticatedClient {
        Transaction t = transactionService.getById(transId);
        Category category = getById(categoryId);

        category.getTransactions().add(t);
        categoryDao.update(category);
        t.setCategory(category);
        transactionDao.update(t);
    }

    private boolean validate(Category category) {
        if (category.getName().trim().isEmpty() || categoryDao.find(category.getId()) != null) {
            return false;
        }
        boolean notExist = true;
        List<Category> categories = getAll();
        //todo sql
        for (Category c : categories) {
            if (c.getName().equals(category.getName())) {
                notExist = false;
                break;
            }
        }
        return notExist;
    }

    public void remove(int id) throws CategoryNotFoundException, NotAuthenticatedClient {
        Category ca = getById(id);
        ca.getTransactions().clear();
        ca.setBudget(null);
        ca.setCreators(null);
        categoryDao.remove(ca);
    }

    public Category updateCategory(int id, Category category) throws CategoryNotFoundException, NotAuthenticatedClient {
        Category c = getById(id);
        c.setName(category.getName());
        return categoryDao.update(c);
    }

    private boolean isCreator(Category category) throws NotAuthenticatedClient {
        User user = userService.isLogged();
        List<User> creators = category.getCreators();
        //todo sql
        for (User creator : creators) {
            if (creator.getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }
}
