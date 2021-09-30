package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.model.NotifyBudget;
import cz.cvut.fel.model.NotifyDebt;
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

    public List<NotifyBudget> getUsersNotifyBudgets() throws NotAuthenticatedClient {
        return notifyBudgetDao.getUsersNotifyBudgets(isLogged().getId());
    }

    public List<NotifyBudget> getUsersNotifyBudgetsByType(TypeNotification typeNotification) throws NotAuthenticatedClient {
        return notifyBudgetDao.getUsersNotifyBudgetsByType(isLogged().getId(), typeNotification);
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
        return notifyBudget.getBudget() != null && notifyBudget.getCreator() != null && notifyBudget.getTypeNotification() != null;
    }

    public void updateNotifyBudget(int notifyBudgetId, NotifyBudget updatedNotifyBudget) throws Exception {
        NotifyBudget notifyDebt = getByIdNotifyBudget(notifyBudgetId);
        if (!validateNotifyBudget(updatedNotifyBudget)) {
            throw new Exception("Not valid NotifyDebt");
        }
        notifyDebt.setCreator(updatedNotifyBudget.getCreator());
        notifyDebt.setBudget(updatedNotifyBudget.getBudget());
        notifyDebt.setTypeNotification(updatedNotifyBudget.getTypeNotification());
        notifyBudgetDao.update(notifyDebt);
    }

    public void removeNotifyBudget(int notifyBudgetId) throws Exception {
        NotifyBudget notifyBudget = getByIdNotifyBudget(notifyBudgetId);
        notifyBudgetDao.remove(notifyBudget);
    }
}
