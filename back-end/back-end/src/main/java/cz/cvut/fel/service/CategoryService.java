package cz.cvut.fel.service;

import cz.cvut.fel.dao.CategoryDao;
import cz.cvut.fel.dao.TransactionDao;
import cz.cvut.fel.dao.UserDao;
import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.CategoryNotFoundException;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.TransactionNotFoundException;
import cz.cvut.fel.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public CategoryService(CategoryDao categoryDao, UserDao userDao, TransactionDao transactionDao) {
        this.categoryDao = categoryDao;
        this.userDao = userDao;
        this.transactionDao = transactionDao;
    }

    public List<Category> getAll() {
        return categoryDao.findAll();
    }

    public Budget getBudget(int catId) throws CategoryNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        return getById(catId).getBudget();
    }

    public Category getById(int id) throws CategoryNotFoundException, UserNotFoundException, NotAuthenticatedClient {
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

    public boolean persist(Category category, int uid) throws UserNotFoundException {
        Objects.requireNonNull(category);
        if (!validate(category))
            return false;
        User u = userDao.find(uid);
        if (u == null) {
            throw new UserNotFoundException();
        }

        category.getCreators().add(u);
        categoryDao.persist(category);
        u.getMyCategories().add(category);
        userDao.update(u);
        return true;
    }

    public void addTransactionToCategory(int transId, int categoryId) throws TransactionNotFoundException, CategoryNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        Transaction t = transactionDao.find(transId);
        if (t == null) {
            throw new TransactionNotFoundException(transId);
        }
        if (!isOwnerOfTransaction(t)) {
            throw new NotAuthenticatedClient();
        }
        Category category = getById(categoryId);

        category.getTransactions().add(t);
        categoryDao.update(category);
        t.setCategory(category);
        transactionDao.update(t);
    }

    private boolean isOwnerOfTransaction(Transaction t) throws UserNotFoundException {
        User user = userDao.find(SecurityUtils.getCurrentUser().getId());
        if (user == null) {
            throw new UserNotFoundException();
        }
        List<BankAccount> bankAccounts = user.getAvailableBankAccounts();
        for (BankAccount bankAccount : bankAccounts) {
            if (bankAccount.getId() == t.getBankAccount().getId()) {
                return true;
            }
        }
        return false;
    }

    private boolean validate(Category category) {
        if (category.getName().trim().isEmpty() || categoryDao.find(category.getId()) != null) {
            return false;
        }
        boolean notExist = true;
        List<Category> categories = getAll();
        for (Category c : categories) {
            if (c.getName().equals(category.getName())) {
                notExist = false;
                break;
            }
        }
        return notExist;
    }


    public void remove(int id) throws CategoryNotFoundException, NotAuthenticatedClient, UserNotFoundException {
        Category ca = getById(id);
        ca.getTransactions().clear();
        ca.setBudget(null);
        ca.setCreators(null);
        categoryDao.remove(ca);
    }

    public Category updateCategory(int id, Category category) throws CategoryNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        Category c = getById(id);
        c.setName(category.getName());
        return categoryDao.update(c);
    }

    private boolean isCreator(Category category) throws UserNotFoundException {
        User user = userDao.find(SecurityUtils.getCurrentUser().getId());
        if (user == null) {
            throw new UserNotFoundException();
        }
        List<User> creators = category.getCreators();
        for (User creator : creators) {
            if (creator.getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }
}
