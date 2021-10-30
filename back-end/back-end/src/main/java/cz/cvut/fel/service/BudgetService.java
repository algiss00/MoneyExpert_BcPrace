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
public class BudgetService extends AbstractServiceHelper {

    public BudgetService(UserDao userDao, BankAccountDao bankAccountDao, TransactionDao transactionDao,
                         BudgetDao budgetDao, DebtDao debtDao, CategoryDao categoryDao,
                         NotifyBudgetDao notifyBudgetDao, NotifyDebtDao notifyDebtDao) {
        super(userDao, bankAccountDao, transactionDao, budgetDao, debtDao, categoryDao, notifyBudgetDao, notifyDebtDao);
    }

    public List<Budget> getAll() {
        return budgetDao.findAll();
    }

    public List<Budget> getByName(int accId, String name) throws Exception {
        return budgetDao.getByName(getByIdBankAccount(accId).getId(), name);
    }

    public Budget persist(Budget budget, int accId, int categoryId) throws
            Exception {
        Objects.requireNonNull(budget);
        User u = getAuthenticatedUser();
        Category category = getByIdCategory(categoryId);

        BankAccount bankAccount = getByIdBankAccount(accId);
        if (!validate(budget, bankAccount.getId(), categoryId)) {
            throw new NotValidDataException("budget");
        }
        budget.setCreator(u);
        budget.setCategory(category);
        budget.setBankAccount(bankAccount);
        budget.setSumAmount(0);
        Budget persistedBudget = budgetDao.persist(budget);
        bankAccount.getBudgets().add(budget);
        bankAccountDao.update(bankAccount);
        return persistedBudget;
    }

    private boolean validate(Budget budget, int bankAccountId, int catId) throws Exception {
        if (budget.getName().trim().isEmpty() || budget.getAmount() <= 0
                || budget.getPercentNotif() > 100) {
            return false;
        }
        return getBudgetByCategoryInBankAcc(catId, bankAccountId) == null;
    }

    public Budget getBudgetByCategoryInBankAcc(int catId, int bankAccId) throws Exception {
        getByIdCategory(catId);
        return budgetDao.getByCategory(catId, bankAccId);
    }

    public Budget updateBudget(int id, Budget budget) throws BudgetNotFoundException, NotAuthenticatedClient {
        Budget b = getByIdBudget(id);
        b.setName(budget.getName());
        b.setAmount(budget.getAmount());
        b.setPercentNotif(budget.getPercentNotif());
        b.setCategory(budget.getCategory());

        return budgetDao.update(b);
    }

    public void removeCategoryFromBudget(int buId) throws BudgetNotFoundException, NotAuthenticatedClient {
        Budget budget = getByIdBudget(buId);
        budget.setCategory(null);
        budgetDao.update(budget);
    }
}
