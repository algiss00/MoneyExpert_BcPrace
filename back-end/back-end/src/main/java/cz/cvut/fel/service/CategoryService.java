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

    /**
     * get all categories.
     *
     * @return
     */
    public List<Category> getAll() {
        return categoryDao.findAll();
    }

    /**
     * get all users created categories.
     *
     * @return
     * @throws NotAuthenticatedClient
     */
    public List<Category> getUsersCreatedCategory() throws NotAuthenticatedClient {
        return categoryDao.getUsersCreatedCategory(getAuthenticatedUser().getId());
    }

    /**
     * get category by name.
     *
     * @param name
     * @return
     * @throws Exception
     */
    public Category getUsersCategoryByName(String name) throws Exception {
        return categoryDao.getUsersCategoryByName(getAuthenticatedUser().getId(), name);
    }

    /**
     * get budget from category.
     *
     * @param catId
     * @return
     * @throws Exception
     */
    public List<Budget> getAllBudgetsFromCategory(int catId) throws Exception {
        return getByIdCategory(catId).getBudget();
    }

    /**
     * get transactions from category.
     *
     * @param catId
     * @return
     * @throws Exception
     */
    public List<Transaction> getTransactions(int catId) throws Exception {
        return getByIdCategory(catId).getTransactions();
    }

    /**
     * Persist category.
     * name of Category is unique
     *
     * @param category
     * @return
     * @throws Exception
     */
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

    /**
     * add transaction to category.
     *
     * @param transId
     * @param categoryId
     * @throws Exception
     */
    public void addTransactionToCategory(int transId, int categoryId) throws Exception {
        Transaction t = getByIdTransaction(transId);
        Category category = getByIdCategory(categoryId);

        category.getTransactions().add(t);
        categoryDao.update(category);
        t.setCategory(category);
        transactionDao.update(t);
    }

    /**
     * validation of category
     *
     * @param category
     * @param user
     * @return
     * @throws Exception
     */
    private boolean validate(Category category, User user) throws Exception {
        if (category.getName().trim().isEmpty()) {
            return false;
        }
        // check if category name is exist in users categories
        return categoryDao.getUsersCategoryByName(user.getId(), category.getName()) == null;
    }

    /**
     * update only name of Category.
     * cannot update default category
     * cannot update name to existed name
     *
     * @param id
     * @param name
     * @return
     * @throws Exception
     */
    public Category updateCategoryName(int id, String name) throws Exception {
        User user = getAuthenticatedUser();
        Category category = getByIdCategory(id);
        if (category.getName().trim().isEmpty()
                || name.trim().equals(category.getName().trim())
                || id < 0) {
            throw new NotValidDataException("Update name of Category not valid");
        }

        if (categoryDao.getUsersCategoryByName(user.getId(), name) != null) {
            throw new NotValidDataException("Update name is exists!");
        }
        category.setName(name);
        return categoryDao.update(category);
    }
}
