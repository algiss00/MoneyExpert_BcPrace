package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.model.*;
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
        Category category = getByIdCategory(categoryId);

        BankAccount bankAccount = getByIdBankAccount(accId);
        if (!validate(budget, bankAccount.getId(), categoryId)) {
            throw new NotValidDataException("budget");
        }
        budget.getCategory().add(category);
        budget.setBankAccount(bankAccount);
        budget.setSumAmount(0);
        Budget persistedBudget = budgetDao.persist(budget);
        bankAccount.getBudgets().add(budget);
        bankAccountDao.update(bankAccount);
        return persistedBudget;
    }

    private boolean validate(Budget budget, int bankAccountId, int catId) throws Exception {
        if (budget.getName().trim().isEmpty() || budget.getAmount() <= 0
                || budget.getPercentNotify() > 100 || budget.getPercentNotify() < 0) {
            return false;
        }
        return getBudgetByCategoryInBankAcc(catId, bankAccountId) == null;
    }

    public Budget getBudgetByCategoryInBankAcc(int catId, int bankAccId) throws Exception {
        return budgetDao.getByCategory(getByIdCategory(catId).getId(), getByIdBankAccount(bankAccId).getId());
    }

    public Budget updateBudgetName(int id, String name) throws Exception {
        Budget budget = getByIdBudget(id);
        if (name.trim().isEmpty()) {
            throw new NotValidDataException("Name of budget is empty!");
        }
        budget.setName(name);
        return budgetDao.update(budget);
    }

    public Budget updateBudgetAmount(int id, Double amount) throws Exception {
        Budget budget = getByIdBudget(id);
        if (amount == null || amount <= 0 || budget.getAmount() == amount) {
            throw new NotValidDataException("amount of budget not valid!");
        }
        updateAmountLogic(budget, amount);

        budget.setAmount(amount);
        checkPercentBudget(budget);

        return budgetDao.update(budget);
    }

    private void checkPercentBudget(Budget budget) throws Exception {
        double percentOfSumAmount = budget.getSumAmount() * 100 / budget.getAmount();
        if (percentOfSumAmount >= budget.getPercentNotify()) {
            createNotifyBudget(budget, TypeNotification.BUDGET_PERCENT);
        } else {
            NotifyBudget notifyBudget = notifyBudgetDao.getBudgetsNotifyBudgetByType(budget.getId(), TypeNotification.BUDGET_PERCENT);
            if (notifyBudget != null) {
                notifyBudgetDao.deleteNotifyBudgetById(notifyBudget.getId());
            }
        }
    }

    public Budget updateBudgetPercent(int id, int percent) throws Exception {
        Budget budget = getByIdBudget(id);
        if (percent > 100 || percent < 0) {
            throw new NotValidDataException("percent of budget not valid!");
        }

        budget.setPercentNotify(percent);
        checkPercentBudget(budget);

        return budgetDao.update(budget);
    }

    private void updateAmountLogic(Budget budget, Double updatedAmount) throws Exception {
        double oldAmount = budget.getAmount();
        double actualSumAmount = budget.getSumAmount();
        if (oldAmount > updatedAmount) {
            if (actualSumAmount >= updatedAmount) {
                NotifyBudget notifyBudget = notifyBudgetDao.getBudgetsNotifyBudgetByType(budget.getId(), TypeNotification.BUDGET_AMOUNT);
                if (notifyBudget == null) {
                    NotifyBudget notifyBudget2 = new NotifyBudget();
                    notifyBudget2.setTypeNotification(TypeNotification.BUDGET_AMOUNT);
                    notifyBudget2.setBudget(budget);
                    notifyBudgetDao.persist(notifyBudget2);
                }
            }
        } else {
            NotifyBudget notifyBudget = notifyBudgetDao.getBudgetsNotifyBudgetByType(budget.getId(), TypeNotification.BUDGET_AMOUNT);
            if (actualSumAmount < updatedAmount) {
                if (notifyBudget != null) {
                    notifyBudgetDao.deleteNotifyBudgetById(notifyBudget.getId());
                }
            } else {
                if (notifyBudget == null) {
                    NotifyBudget notifyBudget2 = new NotifyBudget();
                    notifyBudget2.setTypeNotification(TypeNotification.BUDGET_AMOUNT);
                    notifyBudget2.setBudget(budget);
                    notifyBudgetDao.persist(notifyBudget2);
                }
            }
        }
    }

    public void removeCategoryFromBudget(int buId) throws Exception {
        Budget budget = getByIdBudget(buId);
        budget.setCategory(null);
        budgetDao.update(budget);
    }
}
