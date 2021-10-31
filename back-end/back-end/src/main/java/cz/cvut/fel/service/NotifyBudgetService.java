package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.NotifyBudget;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.NotValidDataException;
import cz.cvut.fel.service.exceptions.NotifyBudgetNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotifyBudgetService extends AbstractServiceHelper {
    public NotifyBudgetService(UserDao userDao, BankAccountDao bankAccountDao, TransactionDao transactionDao,
                               BudgetDao budgetDao, DebtDao debtDao, CategoryDao categoryDao,
                               NotifyBudgetDao notifyBudgetDao, NotifyDebtDao notifyDebtDao) {
        super(userDao, bankAccountDao, transactionDao, budgetDao, debtDao, categoryDao, notifyBudgetDao, notifyDebtDao);
    }

    public List<NotifyBudget> getAll() {
        return notifyBudgetDao.findAll();
    }

    public List<NotifyBudget> getBudgetsNotifyBudgets(int budgetId) throws Exception {
        Budget budget = getByIdBudget(budgetId);
        return notifyBudgetDao.getBudgetsNotifyBudgets(budget.getId());
    }

    public List<NotifyBudget> getNotifyBudgetsFromBankAccount(int bankAccId) throws Exception {
        BankAccount bankAccount = getByIdBankAccount(bankAccId);
        return notifyBudgetDao.getNotifyBudgetsFromBankAccount(bankAccount.getId());
    }

    public List<NotifyBudget> getBudgetsNotifyBudgetByType(int budgetId, TypeNotification typeNotification) throws Exception {
        Budget budget = getByIdBudget(budgetId);
        return notifyBudgetDao.getBudgetsNotifyBudgetByType(budget.getId(), typeNotification);
    }

    public NotifyBudget getByIdNotifyBudget(int id) throws NotifyBudgetNotFoundException {
        NotifyBudget notifyBudget = notifyBudgetDao.find(id);
        if (notifyBudget == null) {
            throw new NotifyBudgetNotFoundException();
        }
        return notifyBudget;
    }

    public NotifyBudget persistNotifyBudget(NotifyBudget notifyBudget) throws Exception {
        if (!validateNotifyBudget(notifyBudget)) {
            throw new NotValidDataException("notifyBudget");
        }
        return notifyBudgetDao.persist(notifyBudget);
    }

    private boolean validateNotifyBudget(NotifyBudget notifyBudget) {
        return notifyBudget.getBudget() != null && notifyBudget.getTypeNotification() != null;
    }

    public void updateNotifyBudget(int notifyBudgetId, NotifyBudget updatedNotifyBudget) throws Exception {
        NotifyBudget notifyDebt = getByIdNotifyBudget(notifyBudgetId);
        if (!validateNotifyBudget(updatedNotifyBudget)) {
            throw new Exception("Not valid NotifyDebt");
        }
        notifyDebt.setBudget(updatedNotifyBudget.getBudget());
        notifyDebt.setTypeNotification(updatedNotifyBudget.getTypeNotification());
        notifyBudgetDao.update(notifyDebt);
    }

    public void removeNotifyBudget(int notifyBudgetId) throws Exception {
        NotifyBudget notifyBudget = getByIdNotifyBudget(notifyBudgetId);
        notifyBudgetDao.remove(notifyBudget);
    }
}
