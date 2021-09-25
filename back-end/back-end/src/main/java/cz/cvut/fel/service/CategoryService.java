package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
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

    public Budget getBudget(int catId) throws Exception {
        return getByIdCategory(catId).getBudget();
    }

    public List<Transaction> getTransactions(int catId) throws Exception {
        return getByIdCategory(catId).getTransactions();
    }

    public boolean persist(Category category) throws Exception {
        Objects.requireNonNull(category);
        User u = isLogged();
        if (!validate(category, u))
            return false;
        // todo
//        int newId = getAll().size() + 1;
//        System.out.println("NEW ID " + newId);
        //category.setId(newId);
        category.getCreators().add(u);
        categoryDao.persist(category);
        u.getMyCategories().add(category);
        userDao.update(u);
        return true;
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
        if (category.getName().trim().isEmpty() || categoryDao.find(category.getId()) != null) {
            return false;
        }
        // check if category name is not exist in users categories
        return categoryDao.getUsersCategoryByName(user.getId(), category.getName()) == null;
    }

    public void remove(int id) throws Exception {
        Category ca = getByIdCategory(id);
        ca.getTransactions().clear();
        ca.setBudget(null);
        ca.setCreators(null);
        categoryDao.remove(ca);
    }

    public Category updateCategory(int id, Category category) throws Exception {
        Category c = getByIdCategory(id);
        c.setName(category.getName());
        return categoryDao.update(c);
    }
}
