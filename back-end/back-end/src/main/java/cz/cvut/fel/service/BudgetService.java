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

    public List<Budget> getByName(int bankAccId, String name) throws Exception {
        return budgetDao.getByName(getByIdBankAccount(bankAccId).getId(), name);
    }

    public Category getCategoryOfBudget(int budgetId) throws Exception {
        return getByIdBudget(budgetId).getCategory().stream().findFirst().orElse(null);
    }

    /**
     * Pridani budgetu do BankAccount s urcitou Category
     * Budget v BankAccount muze patrit jen k jedne Category
     * tzn. V bankAccount muze byt jen jeden budget na jednu Category
     *
     * @param budget
     * @param bankAccId
     * @param categoryId
     * @return
     * @throws Exception
     */
    public Budget persist(Budget budget, int bankAccId, int categoryId) throws
            Exception {
        Objects.requireNonNull(budget);
        Category category = getByIdCategory(categoryId);

        BankAccount bankAccount = getByIdBankAccount(bankAccId);
        if (!validate(budget, bankAccount.getId(), categoryId)) {
            throw new NotValidDataException("persist budget");
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

    public Budget getBudgetByCategoryInBankAcc(int categoryId, int bankAccId) throws Exception {
        return budgetDao.getByCategory(getByIdCategory(categoryId).getId(), getByIdBankAccount(bankAccId).getId());
    }

    /**
     * Update only budget name
     *
     * @param budgetId
     * @param name
     * @return
     * @throws Exception
     */
    public Budget updateBudgetName(int budgetId, String name) throws Exception {
        Budget budget = getByIdBudget(budgetId);
        if (name.trim().isEmpty()) {
            throw new NotValidDataException("Name of budget is empty!");
        }
        budget.setName(name);
        return budgetDao.update(budget);
    }

    /**
     * update only amount of budget
     * Pri tom se dela logika budgetu
     *
     * @param budgetId
     * @param amount
     * @return
     * @throws Exception
     */
    public Budget updateBudgetAmount(int budgetId, Double amount) throws Exception {
        Budget budget = getByIdBudget(budgetId);
        if (amount == null || amount <= 0 || budget.getAmount() == amount) {
            throw new NotValidDataException("amount of budget not valid!");
        }
        updateAmountLogic(budget, amount);

        budget.setAmount(amount);
        checkPercentBudget(budget);

        return budgetDao.update(budget);
    }

    /**
     * Kontrola pokud se nezmenil percent od sumAmount
     *
     * @param budget
     * @throws Exception
     */
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

    /**
     * update only Budget percentNotify
     *
     * @param budgetId
     * @param percent
     * @return
     * @throws Exception
     */
    public Budget updateBudgetPercent(int budgetId, int percent) throws Exception {
        Budget budget = getByIdBudget(budgetId);
        if (percent > 100 || percent < 0) {
            throw new NotValidDataException("percent of budget not valid!");
        }

        budget.setPercentNotify(percent);
        checkPercentBudget(budget);

        return budgetDao.update(budget);
    }

    /**
     * Logika pri update Amount
     * Kontrolujou se tady NotifyBudget entity
     *
     * @param budget
     * @param updatedAmount
     * @throws Exception
     */
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
