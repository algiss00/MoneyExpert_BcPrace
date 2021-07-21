package cz.cvut.fel.service;

import cz.cvut.fel.dao.CategoryDao;
import cz.cvut.fel.dao.TransactionDao;
import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.Category;
import cz.cvut.fel.model.Transaction;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.CategoryNotFoundException;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.TransactionNotFoundException;
import cz.cvut.fel.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {
    private CategoryDao categoryDao;
    private UserService userService;
    private TransactionDao transactionDao;

    @Autowired
    public CategoryService(CategoryDao categoryDao, UserService userService, TransactionDao transactionDao) {
        this.categoryDao = categoryDao;
        this.userService = userService;
        this.transactionDao = transactionDao;
    }

    public List<Category> getAll() {
        return categoryDao.findAll();
    }

    public Budget getBudget(int budId) throws CategoryNotFoundException {
        return getById(budId).getBudget();
    }

    public List<Category> getAllUsersCategories(int uid) {
        return categoryDao.getAllUsersCategories(uid);
    }

    public Category getById(int id) throws CategoryNotFoundException {
        Category u = categoryDao.find(id);
        if (u == null) {
            throw new CategoryNotFoundException(id);
        }
        return u;
    }

    public boolean persist(Category category, int uid) throws UserNotFoundException {
        User u = userService.getById(uid);
        if (category == null)
            throw new NullPointerException("category can not be Null.");
        if (!validate(category))
            return false;
        category.setCreator(u);
        categoryDao.persist(category);
        return true;
    }

    public List<Transaction> addTransactionToCategory(int transId, int categoryId) throws TransactionNotFoundException, CategoryNotFoundException {
        Transaction t = transactionDao.find(transId);
        if (t == null) {
            throw new TransactionNotFoundException(transId);
        }
        Category category = getById(categoryId);

        category.getTransactions().add(t);
        categoryDao.update(category);
        t.setCategory(category);
        transactionDao.update(t);
        return category.getTransactions();
    }

    public boolean validate(Category category) {
        return !category.getName().trim().isEmpty();
    }


    public void remove(int id) throws CategoryNotFoundException, NotAuthenticatedClient {
        if (SecurityUtils.getCurrentUser() == null) {
            throw new NotAuthenticatedClient();
        }
        Category ca = getById(id);
        ca.getTransactions().clear();
        ca.setBudget(null);
        ca.setCreator(null);
        categoryDao.remove(ca);
    }

//    public boolean alreadyExists(Category category) {
//        return categoryDao.find(category.getId()) != null;
//    }

    public Category updateCategory(int id, Category category) throws CategoryNotFoundException {
        Category c = getById(id);
        c.setName(category.getName());
        return categoryDao.update(c);
    }

}
