package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.NotValidDataException;
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

    public List<Category> getUsersCreatedCategory() throws NotAuthenticatedClient {
        return categoryDao.getUsersCreatedCategory(getAuthenticatedUser().getId());
    }

    public List<Budget> getAllBudgetsFromCategory(int catId) throws Exception {
        return getByIdCategory(catId).getBudget();
    }

    public List<Transaction> getTransactions(int catId) throws Exception {
        return getByIdCategory(catId).getTransactions();
    }

    public Category persist(Category category) throws Exception {
        Objects.requireNonNull(category);
        User u = getAuthenticatedUser();
        if (!validate(category, u))
            throw new NotValidDataException("category");
        category.getCreators().add(u);
        Category persistedCategory = categoryDao.persist(category);
        u.getMyCategories().add(category);
        userDao.update(u);
        return persistedCategory;
    }

    public void addTransactionToCategory(int transId, int categoryId) throws Exception {
        Transaction t = getByIdTransaction(transId);
        Category category = getByIdCategory(categoryId);

        category.getTransactions().add(t);
        categoryDao.update(category);
        t.setCategory(category);
        transactionDao.update(t);
    }

    private boolean validate(Category category, User user) throws Exception {
        if (category.getName().trim().isEmpty()) {
            return false;
        }
        // check if category name is exist in users categories
        return categoryDao.getUsersCategoryByName(user.getId(), category.getName()) == null;
    }

    public Category updateCategoryName(int id, String name) throws Exception {
        Category c = getByIdCategory(id);
        c.setName(name);
        return categoryDao.update(c);
    }
}
