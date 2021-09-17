package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
import cz.cvut.fel.service.exceptions.BankAccountNotFoundException;
import cz.cvut.fel.service.exceptions.DebtNotFoundException;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class DebtService {
    private DebtDao debtDao;
    private UserDao userDao;
    private BankAccountDao bankAccountDao;
    private NotifyDebtDao notifyDebtDao;
    private UserService userService;
    private BankAccountService bankAccountService;

    public DebtService(DebtDao debtDao, UserDao userDao, BankAccountDao bankAccountDao, NotifyDebtDao notifyDebtDao,
                       CategoryDao categoryDao, TransactionDao transactionDao, BudgetDao budgetDao, NotifyBudgetDao notifyBudgetDao) {
        this.debtDao = debtDao;
        this.userDao = userDao;
        this.bankAccountDao = bankAccountDao;
        this.notifyDebtDao = notifyDebtDao;
        this.userService = new UserService(userDao);
        this.bankAccountService = new BankAccountService(categoryDao, bankAccountDao, userDao, transactionDao, budgetDao, debtDao, notifyBudgetDao);
    }

    public List<Debt> getAll() {
        return debtDao.findAll();
    }

    public Debt getById(int id) throws DebtNotFoundException, NotAuthenticatedClient {
        Debt d = debtDao.find(id);
        if (d == null) {
            throw new DebtNotFoundException(id);
        }
        if (!isCreator(d)) {
            throw new NotAuthenticatedClient();
        }
        return d;
    }

    public boolean persist(Debt debt, int accId) throws BankAccountNotFoundException, NotAuthenticatedClient {
        User u = userService.isLogged();
        BankAccount bankAccount = bankAccountService.getById(accId);
        Objects.requireNonNull(debt);
        if (!validate(debt))
            return false;
        debt.setCreator(u);
        debt.setBankAccount(bankAccount);
        debtDao.persist(debt);
        return true;
    }

    //todo v sql request
    //todo fetch debts new table
    //every 12 hours
    @Scheduled(cron = "5 * * * * * ")
    public void checkNotifyDates() {
        System.out.println("NOTiFY");
        //check if exists, maybe every 12 hours notificate check
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

    private boolean notifyDebtExits(int notifiedDebtId, TypeNotification type) {
        return notifyDebtDao.alreadyExistsDebt(notifiedDebtId, type) != null;
    }

    @Scheduled(cron = "6 * * * * * ")
    public void checkDeadlineDates() {
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

    private boolean validate(Debt debt) {
        if (debt.getName().trim().isEmpty() || debtDao.find(debt.getId()) != null) {
            return false;
        }
        boolean notExist = true;
        List<Debt> debts = getAll();
        //todo sql
        for (Debt d : debts) {
            if (d.getName().equals(debt.getName())) {
                notExist = false;
                break;
            }
        }
        return notExist;
    }

    public void remove(int id) throws NotAuthenticatedClient, DebtNotFoundException {
        Debt debt = getById(id);
        debtDao.remove(debt);
    }

    public Debt updateDebt(int id, Debt debt) throws DebtNotFoundException, NotAuthenticatedClient {
        Debt da = getById(id);

        da.setName(debt.getName());
        da.setAmount(debt.getAmount());
        da.setDescription(debt.getDescription());
        da.setDeadline(debt.getDeadline());
        da.setReplay(debt.getReplay());
        da.setNotifyDate(debt.getNotifyDate());

        return debtDao.update(da);
    }

    private boolean isCreator(Debt debt) throws NotAuthenticatedClient {
        User user = userService.isLogged();
        return debt.getCreator().getId() == user.getId();
    }
}
