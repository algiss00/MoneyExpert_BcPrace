package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.model.*;
import cz.cvut.fel.service.exceptions.NotValidDataException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Logger;

@Service
@Transactional
public class DebtService extends AbstractServiceHelper {
    private static final Logger log = Logger.getLogger(DebtService.class.getName());

    public DebtService(UserDao userDao, BankAccountDao bankAccountDao, TransactionDao transactionDao,
                       BudgetDao budgetDao, DebtDao debtDao, CategoryDao categoryDao,
                       NotifyBudgetDao notifyBudgetDao, NotifyDebtDao notifyDebtDao) {
        super(userDao, bankAccountDao, transactionDao, budgetDao, debtDao, categoryDao, notifyBudgetDao, notifyDebtDao);
    }

    /**
     * get debt by name from bankAccount.
     *
     * @param bankAccountId
     * @param name
     * @return
     * @throws Exception
     */
    public List<Debt> getByNameFromBankAcc(int bankAccountId, String name) throws Exception {
        BankAccount bankAccount = getByIdBankAccount(bankAccountId);
        return debtDao.getByNameFromBankAcc(bankAccount.getId(), name);
    }

    /**
     * Persist debt to BankAccount.
     *
     * @param debt
     * @param bankAccountId
     * @return
     * @throws Exception
     */
    public Debt persist(Debt debt, int bankAccountId) throws Exception {
        BankAccount bankAccount = getByIdBankAccount(bankAccountId);
        Objects.requireNonNull(debt);
        if (!validate(debt))
            throw new NotValidDataException("debt");
        debt.setBankAccount(bankAccount);
        debt.setDeadline(debt.getDeadline());
        debt.setNotifyDate(debt.getNotifyDate());
        return debtDao.persist(debt);
    }

    /**
     * Scheduled function,  which is called every 6 hours.
     * Checks all Debts for which NotifyDate has occurred
     * then those find will add to the NotifyDebt entity
     */
    //every 6 hours - "0 0 */6 * * * "
    @Scheduled(cron = "0 0 */6 * * * ")
    public void checkNotifyDates() {
        log.info("check DEBT NotifyDate");
        List<Debt> notifyDebts = debtDao.getNotifyDebts();
        if (notifyDebts.isEmpty()) {
            return;
        }

        for (Debt notifiedDebt : notifyDebts) {
            if (notifyDebtExits(notifiedDebt.getId(), TypeNotification.DEBT_NOTIFY)) {
                continue;
            }
            NotifyDebt notifyDebtEntity = new NotifyDebt();
            notifyDebtEntity.setDebt(notifiedDebt);
            notifyDebtEntity.setTypeNotification(TypeNotification.DEBT_NOTIFY);

            notifyDebtDao.persist(notifyDebtEntity);
            log.info("DEBT added to NotifyDebt with notify type " + notifyDebtEntity.getDebt().getName());
        }
    }

    /**
     * check if NotifyDebt with this type exists.
     *
     * @param notifiedDebtId
     * @param type
     * @return
     */
    private boolean notifyDebtExits(int notifiedDebtId, TypeNotification type) {
        return notifyDebtDao.alreadyExistsDebt(notifiedDebtId, type) != null;
    }

    /**
     * Scheduled function,  which is called every 6 hours.
     * Checks all Debts for which Deadline date has occurred
     * then those find will add to the NotifyDebt entity
     */
    @Scheduled(cron = "0 0 */6 * * * ")
    public void checkDeadlineDates() {
        log.info("check DEBT DEADLINE date");
        List<Debt> deadlineDebts = debtDao.getDeadlineDebts();
        if (deadlineDebts.isEmpty()) {
            return;
        }

        for (Debt notifiedDebt : deadlineDebts) {
            if (notifyDebtExits(notifiedDebt.getId(), TypeNotification.DEBT_DEADLINE)) {
                continue;
            }
            NotifyDebt notifyDebtEntity = new NotifyDebt();
            notifyDebtEntity.setDebt(notifiedDebt);
            notifyDebtEntity.setTypeNotification(TypeNotification.DEBT_DEADLINE);

            notifyDebtDao.persist(notifyDebtEntity);
            log.info("DEBT added to NotifyDebt with deadline type " + notifyDebtEntity.getDebt().getName());
        }
    }

    /**
     * Validation if debt.
     *
     * @param debt
     * @return
     */
    private boolean validate(Debt debt) {
        if (debt.getName().trim().isEmpty() || debt.getAmount() <= 0 || debt.getDeadline().before(debt.getNotifyDate())) {
            return false;
        }
        return true;
    }

    /**
     * update Debt only name amount and description.
     *
     * @param id
     * @param updatedDebt
     * @return
     * @throws Exception
     */
    public Debt updateDebtBasic(int id, Debt updatedDebt) throws Exception {
        Debt debt = getByIdDebt(id);

        debt.setName(updatedDebt.getName());
        debt.setAmount(updatedDebt.getAmount());
        debt.setDescription(updatedDebt.getDescription());

        return debtDao.update(debt);
    }

    /**
     * update only NotifyDate.
     * deadline cannot be before the NotifyDate
     *
     * @param id
     * @param notifyDate
     * @return
     * @throws Exception
     */
    public Debt updateDebtNotifyDate(int id, Date notifyDate) throws Exception {
        Debt debt = getByIdDebt(id);

        if (debt.getDeadline().before(notifyDate)) {
            throw new NotValidDataException("Notify date is after deadline date!");
        }
        updateDebtNotifyDateLogic(debt, "NotifyDate");
        debt.setNotifyDate(notifyDate);

        return debtDao.update(debt);
    }

    /**
     * logic for updating NotifyDate.
     * check NotifyDebts
     *
     * @param debt
     * @param typeOfNotify
     * @throws Exception
     */
    private void updateDebtNotifyDateLogic(Debt debt, String typeOfNotify) throws Exception {
        if (typeOfNotify.equals("NotifyDate")) {
            NotifyDebt notifyDebt = notifyDebtDao.getDebtsNotifyDebtsByType(debt.getId(), TypeNotification.DEBT_NOTIFY);
            if (notifyDebt != null) {
                notifyDebtDao.deleteNotifyDebtByDebtIdAndType(debt.getId(), TypeNotification.DEBT_NOTIFY);
            }
        } else if (typeOfNotify.equals("Deadline")) {
            NotifyDebt notifyDebt = notifyDebtDao.getDebtsNotifyDebtsByType(debt.getId(), TypeNotification.DEBT_DEADLINE);
            if (notifyDebt != null) {
                notifyDebtDao.deleteNotifyDebtByDebtIdAndType(debt.getId(), TypeNotification.DEBT_DEADLINE);
            }
        }

    }

    /**
     * update only Deadline.
     * deadline cannot be before the NotifyDate
     *
     * @param id
     * @param deadline
     * @return
     * @throws Exception
     */
    public Debt updateDebtDeadlineDate(int id, Date deadline) throws Exception {
        Debt debt = getByIdDebt(id);

        if (debt.getNotifyDate().after(deadline)) {
            throw new NotValidDataException("Deadline date is before notifyDate date!");
        }

        updateDebtNotifyDateLogic(debt, "Deadline");
        debt.setDeadline(deadline);

        return debtDao.update(debt);
    }
}
