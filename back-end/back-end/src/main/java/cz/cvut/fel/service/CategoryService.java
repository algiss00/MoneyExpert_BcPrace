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
public class CategoryService extends AbstractServiceHelper {

    public CategoryService(UserDao userDao, BankAccountDao bankAccountDao, TransactionDao transactionDao,
                           BudgetDao budgetDao, DebtDao debtDao, CategoryDao categoryDao,
                           NotifyBudgetDao notifyBudgetDao, NotifyDebtDao notifyDebtDao) {
        super(userDao, bankAccountDao, transactionDao, budgetDao, debtDao, categoryDao, notifyBudgetDao, notifyDebtDao);
    }

    public List<Category> getAll() {
        return categoryDao.findAll();
    }

    public Budget getBudget(String catId) throws CategoryNotFoundException, NotAuthenticatedClient {
        return getByIdCategory(catId).getBudget();
    }

    public List<Transaction> getTransactions(String catId) throws CategoryNotFoundException, NotAuthenticatedClient {
        return getByIdCategory(catId).getTransactions();
    }

    public boolean persist(Category category) throws NotAuthenticatedClient {
        Objects.requireNonNull(category);
        User u = isLogged();
        if (!validate(category, u))
            return false;
        category.getCreators().add(u);
        categoryDao.persist(category);
        u.getMyCategories().add(category);
        userDao.update(u);
        return true;
    }

    public void addTransactionToCategory(String transId, String categoryId) throws TransactionNotFoundException, CategoryNotFoundException, NotAuthenticatedClient {
        Transaction t = getByIdTransaction(transId);
        Category category = getByIdCategory(categoryId);

        category.getTransactions().add(t);
        categoryDao.update(category);
        t.setCategory(category);
        transactionDao.update(t);
    }

    private boolean validate(Category category, User user) {
        if (category.getName().trim().isEmpty() || categoryDao.find(category.getId()) != null) {
            return false;
        }
        boolean notExist = true;
        // kontrol if category name is not exist in users categories
        List<Category> usersCategories = user.getMyCategories();
        //todo sql - get by name from users categories
        for (Category c : usersCategories) {
            if (c.getName().equals(category.getName())) {
                notExist = false;
                break;
            }
        }
        return notExist;
    }

    public void remove(String id) throws CategoryNotFoundException, NotAuthenticatedClient {
        Category ca = getByIdCategory(id);
        ca.getTransactions().clear();
        ca.setBudget(null);
        ca.setCreators(null);
        categoryDao.remove(ca);
    }

    public Category updateCategory(String id, Category category) throws CategoryNotFoundException, NotAuthenticatedClient {
        Category c = getByIdCategory(id);
        c.setName(category.getName());
        return categoryDao.update(c);
    }
}
