package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Debt;
import cz.cvut.fel.model.NotifyDebt;
import cz.cvut.fel.service.exceptions.NotValidDataException;
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

    /**
     * get All notifyDebts.
     *
     * @return
     */
    public List<NotifyDebt> getAll() {
        return notifyDebtDao.findAll();
    }

    /**
     * get notifyDebts by debt id.
     *
     * @param debtId
     * @return
     * @throws Exception
     */
    public List<NotifyDebt> getDebtsNotifyDebts(int debtId) throws Exception {
        Debt debt = getByIdDebt(debtId);
        return notifyDebtDao.getNotifyDebtByDebtId(debt.getId());
    }

    /**
     * get notifyDebt by debt id and typeNotification.
     *
     * @param debtId
     * @param typeNotification
     * @return
     * @throws Exception
     */
    public NotifyDebt getDebtsNotifyDebtsByType(int debtId, TypeNotification typeNotification) throws Exception {
        Debt debt = getByIdDebt(debtId);
        return notifyDebtDao.getDebtsNotifyDebtsByType(debt.getId(), typeNotification);
    }

    /**
     * get notifyDebts from BankAccount.
     *
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public List<NotifyDebt> getNotifyDebtsFromBankAccount(int bankAccId) throws Exception {
        BankAccount bankAccount = getByIdBankAccount(bankAccId);
        return notifyDebtDao.getNotifyDebtsFromBankAccount(bankAccount.getId());
    }

    /**
     * get notifyDebt by id.
     *
     * @param id
     * @return
     * @throws NotifyDebtNotFoundException
     */
    public NotifyDebt getByIdNotifyDebt(int id) throws NotifyDebtNotFoundException {
        NotifyDebt notifyDebt = notifyDebtDao.find(id);
        if (notifyDebt == null) {
            throw new NotifyDebtNotFoundException();
        }
        return notifyDebt;
    }

    /**
     * persist notifyDebt.
     *
     * @param notifyDebt
     * @return
     * @throws Exception
     */
    public NotifyDebt persistNotifyDebt(NotifyDebt notifyDebt) throws Exception {
        if (!validateNotifyDebt(notifyDebt)) {
            throw new NotValidDataException("notifyDebt");
        }
        return notifyDebtDao.persist(notifyDebt);
    }

    /**
     * validation.
     *
     * @param notifyDebt
     * @return
     */
    private boolean validateNotifyDebt(NotifyDebt notifyDebt) {
        return notifyDebt.getDebt() != null && notifyDebt.getTypeNotification() != null;
    }

    /**
     * update notifyDebt.
     *
     * @param notifyDebtId
     * @param updatedNotifyDebt
     * @throws Exception
     */
    public void updateNotifyDebt(int notifyDebtId, NotifyDebt updatedNotifyDebt) throws Exception {
        NotifyDebt notifyDebt = getByIdNotifyDebt(notifyDebtId);
        if (!validateNotifyDebt(updatedNotifyDebt)) {
            throw new Exception("Not valid NotifyDebt");
        }
        notifyDebt.setDebt(updatedNotifyDebt.getDebt());
        notifyDebt.setTypeNotification(updatedNotifyDebt.getTypeNotification());
        notifyDebtDao.update(notifyDebt);
    }

    /**
     * remove notifyDebt from db.
     *
     * @param notifyDebtId
     * @throws Exception
     */
    public void removeNotifyDebt(int notifyDebtId) throws Exception {
        NotifyDebt notifyDebt = getByIdNotifyDebt(notifyDebtId);
        notifyDebtDao.remove(notifyDebt);
    }
}
