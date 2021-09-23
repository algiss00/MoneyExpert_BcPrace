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

    public boolean persist(Budget budget, String accId, String categoryId) throws
            BankAccountNotFoundException, CategoryNotFoundException, NotAuthenticatedClient {
        Objects.requireNonNull(budget);
        User u = isLogged();
        Category category = getByIdCategory(categoryId);

        BankAccount bankAccount = getByIdBankAccount(accId);
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
                && !isBudgetCategoryExist(budget.getCategory(), bankAccount.getId());
    }

    private boolean isBudgetCategoryExist(Category budCategory, String bankAccId) {
        return budgetDao.getByCategory(budCategory.getId(), bankAccId) != null;
    }

    public boolean remove(String id) throws BudgetNotFoundException, NotAuthenticatedClient {
        Budget bu = getByIdBudget(id);
        budgetDao.remove(bu);
        return true;
    }

    public Budget updateBudget(String id, Budget budget) throws BudgetNotFoundException, NotAuthenticatedClient {
        Budget b = getByIdBudget(id);
        b.setName(budget.getName());
        //todo logic of amount
        b.setAmount(budget.getAmount());
        b.setPercentNotif(budget.getPercentNotif());
        b.setCategory(budget.getCategory());

        return budgetDao.update(b);
    }

    public void removeCategoryFromBudget(String buId) throws BudgetNotFoundException, NotAuthenticatedClient {
        Budget budget = getByIdBudget(buId);
        budget.setCategory(null);
        budgetDao.update(budget);
    }
}
