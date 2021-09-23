package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.NotifyDebt;
import cz.cvut.fel.service.exceptions.NotifyDebtNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotifyDebtService extends AbstractServiceHelper {
    public NotifyDebtService(UserDao userDao, BankAccountDao bankAccountDao, TransactionDao transactionDao,
                             BudgetDao budgetDao, DebtDao debtDao, CategoryDao categoryDao,
                             NotifyBudgetDao notifyBudgetDao, NotifyDebtDao notifyDebtDao) {
        super(userDao, bankAccountDao, transactionDao, budgetDao, debtDao, categoryDao, notifyBudgetDao, notifyDebtDao);
    }

    public List<NotifyDebt> getAll() {
        return notifyDebtDao.findAll();
    }

    public NotifyDebt getByIdNotifyDebt(String id) throws NotifyDebtNotFoundException {
        NotifyDebt notifyDebt = notifyDebtDao.find(id);
        if (notifyDebt == null) {
            throw new NotifyDebtNotFoundException();
        }
        return notifyDebt;
    }

    public void persistNotifyDebt(NotifyDebt notifyDebt) throws Exception {
        if (!validateNotifyDebt(notifyDebt)) {
            throw new Exception("Not valid NotifyDebt");
        }
        notifyDebtDao.persist(notifyDebt);
    }

    private boolean validateNotifyDebt(NotifyDebt notifyDebt) {
        return notifyDebt.getDebt() != null && notifyDebt.getCreator() != null && notifyDebt.getTypeNotification() != null;
    }

    public void updateNotifyDebt(String notifyDebtId, NotifyDebt updatedNotifyDebt) throws Exception {
        NotifyDebt notifyDebt = getByIdNotifyDebt(notifyDebtId);
        if (!validateNotifyDebt(updatedNotifyDebt)) {
            throw new Exception("Not valid NotifyDebt");
        }
        notifyDebt.setCreator(updatedNotifyDebt.getCreator());
        notifyDebt.setDebt(updatedNotifyDebt.getDebt());
        notifyDebt.setTypeNotification(updatedNotifyDebt.getTypeNotification());
        notifyDebtDao.update(notifyDebt);
    }

    public void removeNotifyDebt(String notifyDebtId) throws Exception {
        NotifyDebt notifyDebt = getByIdNotifyDebt(notifyDebtId);
        notifyDebtDao.remove(notifyDebt);
    }
}
