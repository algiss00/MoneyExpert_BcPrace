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

    public List<Debt> getByNameFromBankAcc(int bankAccountId, String name) throws Exception {
        BankAccount bankAccount = getByIdBankAccount(bankAccountId);
        return debtDao.getByNameFromBankAcc(bankAccount.getId(), name);
    }

    /**
     * Persist debt to BankAccount
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
        return debtDao.persist(debt);
    }

    /**
     * Scheduled funkce, ktera se vola kazde 6 hodin
     * Kontroluje vsechny Debts u kterych nastal NotifyDate
     * pak ty co najde prida do NotifyDebt entity
     */
    //every 6 hours - "0 0 */6 * * * "
    // every 0:05, 1:05, 2:05 ... to bylo bez "/" v sekundach
    @Scheduled(cron = "30 * * * * * ")
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

    private boolean notifyDebtExits(int notifiedDebtId, TypeNotification type) {
        return notifyDebtDao.alreadyExistsDebt(notifiedDebtId, type) != null;
    }

    /**
     * Scheduled funkce, ktera se vola kazde 6 hodin
     * Kontroluje vsechny Debts u kterych nastal Deadline
     * pak ty co najde prida do NotifyDebt entity
     */
    @Scheduled(cron = "*/20 * * * * * ")
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

    private boolean validate(Debt debt) {
        if (debt.getName().trim().isEmpty() || debt.getAmount() <= 0 || debt.getDeadline().before(debt.getNotifyDate())) {
            return false;
        }
        return true;
    }

    /**
     * update Debt only name amount and description
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
     * update only NotifyDate
     * nelze editovat na vetsi nez deadline datum
     *
     * @param id
     * @param notifyDate
     * @return
     * @throws Exception
     */
    public Debt updateDebtNotifyDate(int id, String notifyDate) throws Exception {
        Debt debt = getByIdDebt(id);
        Date updatedNotifyDate = new SimpleDateFormat("yyyy-MM-dd").parse(notifyDate);

        if (debt.getDeadline().before(updatedNotifyDate)) {
            throw new NotValidDataException("Notify date is after deadline date!");
        }
        updateDebtNotifyDateLogic(debt, updatedNotifyDate, "NotifyDate");
        debt.setNotifyDate(updatedNotifyDate);

        return debtDao.update(debt);
    }

    /**
     * logic of updating NotifyDate
     * check NotifyDebts
     *
     * @param debt
     * @param updatedNotifyDate
     * @param typeOfNotify
     * @throws Exception
     */
    private void updateDebtNotifyDateLogic(Debt debt, Date updatedNotifyDate, String typeOfNotify) throws Exception {
        if (typeOfNotify.equals("NotifyDate")) {
            if (debt.getNotifyDate().before(updatedNotifyDate)) {
                NotifyDebt notifyDebt = notifyDebtDao.getDebtsNotifyDebtsByType(debt.getId(), TypeNotification.DEBT_NOTIFY);
                if (notifyDebt != null) {
                    notifyDebtDao.deleteNotifyDebtByDebtIdAndType(debt.getId(), TypeNotification.DEBT_NOTIFY);
                }
            }
        } else if (typeOfNotify.equals("Deadline")) {
            if (debt.getDeadline().before(updatedNotifyDate)) {
                NotifyDebt notifyDebt = notifyDebtDao.getDebtsNotifyDebtsByType(debt.getId(), TypeNotification.DEBT_DEADLINE);
                if (notifyDebt != null) {
                    notifyDebtDao.deleteNotifyDebtByDebtIdAndType(debt.getId(), TypeNotification.DEBT_DEADLINE);
                }
            }
        }

    }

    /**
     * update only Deadline
     *
     * @param id
     * @param deadline
     * @return
     * @throws Exception
     */
    public Debt updateDebtDeadlineDate(int id, String deadline) throws Exception {
        Debt debt = getByIdDebt(id);
        Date updatedDeadline = new SimpleDateFormat("yyyy-MM-dd").parse(deadline);

        if (debt.getNotifyDate().after(updatedDeadline)) {
            throw new NotValidDataException("Deadline date is after notifyDate date!");
        }

        updateDebtNotifyDateLogic(debt, updatedDeadline, "Deadline");
        debt.setDeadline(updatedDeadline);

        return debtDao.update(debt);
    }
}
