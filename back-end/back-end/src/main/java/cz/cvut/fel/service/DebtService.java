package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.model.*;
import cz.cvut.fel.service.exceptions.DebtNotFoundException;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.NotValidDataException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class DebtService extends AbstractServiceHelper {

    public DebtService(UserDao userDao, BankAccountDao bankAccountDao, TransactionDao transactionDao,
                       BudgetDao budgetDao, DebtDao debtDao, CategoryDao categoryDao,
                       NotifyBudgetDao notifyBudgetDao, NotifyDebtDao notifyDebtDao) {
        super(userDao, bankAccountDao, transactionDao, budgetDao, debtDao, categoryDao, notifyBudgetDao, notifyDebtDao);
    }

    public List<Debt> getAll() {
        return debtDao.findAll();
    }

    public Debt getByName(String name) throws Exception {
        User u = getAuthenticatedUser();
        return debtDao.getByName(u.getId(), name);
    }

    public Debt persist(Debt debt, int accId) throws Exception {
        User u = getAuthenticatedUser();
        BankAccount bankAccount = getByIdBankAccount(accId);
        Objects.requireNonNull(debt);
        if (!validate(debt, u))
            throw new NotValidDataException("debt");
        debt.setCreator(u);
        debt.setBankAccount(bankAccount);
        return debtDao.persist(debt);
    }

    //every 12 hours - "0 0 */12 * * * "
    // every 0:05, 1:05, 2:05 ... to bylo bez "/" v sekundach
    @Scheduled(cron = "*/5 * * * * * ")
    public void checkNotifyDates() throws Exception {
        System.out.println("NOTiFY");
        List<Debt> notifyDebts = debtDao.getNotifyDebts();
        if (notifyDebts.isEmpty()) {
            System.out.println("EMPTY");
            return;
        }
        notifyDebts.forEach(debt -> System.out.println("LIST: " + debt.getName()));
        for (Debt notifiedDebt : notifyDebts) {
            if (notifyDebtExits(notifiedDebt.getId(), TypeNotification.DEBT_NOTIFY)) {
                System.out.println("Already exists");
                continue;
            }
            NotifyDebt notifyDebtEntity = new NotifyDebt();
            notifyDebtEntity.setCreator(notifiedDebt.getCreator());
            notifyDebtEntity.setDebt(notifiedDebt);
            notifyDebtEntity.setTypeNotification(TypeNotification.DEBT_NOTIFY);

            notifyDebtDao.persist(notifyDebtEntity);
            System.out.println("ADDED TO NOTIFY " + notifyDebtEntity.getDebt().getName());
        }
    }

    private boolean notifyDebtExits(int notifiedDebtId, TypeNotification type) throws Exception {
        return notifyDebtDao.alreadyExistsDebt(notifiedDebtId, type) != null;
    }

    @Scheduled(cron = "*/6 * * * * * ")
    public void checkDeadlineDates() throws Exception {
        System.out.println("DEADLINE");
        List<Debt> deadlineDebts = debtDao.getDeadlineDebts();
        if (deadlineDebts.isEmpty()) {
            System.out.println("EMPTY");
            return;
        }
        deadlineDebts.forEach(debt -> System.out.println("LIST: " + debt.getName()));

        for (Debt notifiedDebt : deadlineDebts) {
            if (notifyDebtExits(notifiedDebt.getId(), TypeNotification.DEBT_DEADLINE)) {
                System.out.println("Already exists");
                continue;
            }
            NotifyDebt notifyDebtEntity = new NotifyDebt();
            notifyDebtEntity.setCreator(notifiedDebt.getCreator());
            notifyDebtEntity.setDebt(notifiedDebt);
            notifyDebtEntity.setTypeNotification(TypeNotification.DEBT_DEADLINE);

            notifyDebtDao.persist(notifyDebtEntity);
            System.out.println("ADDED TO NOTIFY " + notifyDebtEntity.getDebt().getName());
        }
    }

    private boolean validate(Debt debt, User user) throws Exception {
        if (debt.getName().trim().isEmpty()) {
            return false;
        }
        return debtDao.getByName(user.getId(), debt.getName()) == null;
    }

    public void remove(int id) throws NotAuthenticatedClient, DebtNotFoundException {
        Debt debt = getByIdDebt(id);
        debtDao.remove(debt);
    }

    public Debt updateDebt(int id, Debt debt) throws DebtNotFoundException, NotAuthenticatedClient {
        Debt da = getByIdDebt(id);

        da.setName(debt.getName());
        da.setAmount(debt.getAmount());
        da.setDescription(debt.getDescription());
        da.setDeadline(debt.getDeadline());
        da.setNotifyDate(debt.getNotifyDate());

        return debtDao.update(da);
    }
}
