package cz.cvut.fel.service;

import cz.cvut.fel.dao.CategoryDao;
import cz.cvut.fel.dao.TransactionDao;
import cz.cvut.fel.dao.UserDao;
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

    public Budget getBudget(int budId) throws CategoryNotFoundException {
        return getById(budId).getBudget();
    }

    //todo
//    public List<Category> getAllUsersCategories(int uid) {
//        return categoryDao.getAllUsersCategories(uid);
//    }

    public Category getById(int id) throws CategoryNotFoundException {
        Category u = categoryDao.find(id);
        if (u == null) {
            throw new CategoryNotFoundException(id);
        }
        return u;
    }

    public boolean persist(Category category, int uid) throws UserNotFoundException {
        if (category == null)
            throw new NullPointerException("category can not be Null.");
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

    public void addTransactionToCategory(int transId, int categoryId) throws TransactionNotFoundException, CategoryNotFoundException {
        Transaction t = transactionDao.find(transId);
        if (t == null) {
            throw new TransactionNotFoundException(transId);
        }
        Category category = getById(categoryId);

        category.getTransactions().add(t);
        categoryDao.update(category);
        t.setCategory(category);
        transactionDao.update(t);
    }

    public boolean validate(Category category) {
        return !category.getName().trim().isEmpty();
    }


    public void remove(int id) throws CategoryNotFoundException, NotAuthenticatedClient {
        Category ca = getById(id);
        if (!isCreator(SecurityUtils.getCurrentUser(), ca)) {
            throw new NotAuthenticatedClient();
        }
        ca.getTransactions().clear();
        ca.setBudget(null);
        ca.setCreators(null);
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

    private boolean isCreator(User user, Category category) {
        List<User> creators = category.getCreators();
        for (User creator : creators) {
            if (creator == user) {
                return true;
            }
        }
        return false;
    }
}
