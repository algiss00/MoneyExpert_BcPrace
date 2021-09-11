package cz.cvut.fel.service;

import cz.cvut.fel.dao.BankAccountDao;
import cz.cvut.fel.dao.DebtDao;
import cz.cvut.fel.dao.NotifyDao;
import cz.cvut.fel.dao.UserDao;
import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.BankAccountNotFoundException;
import cz.cvut.fel.service.exceptions.DebtNotFoundException;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class DebtService {
    private DebtDao debtDao;
    private UserDao userDao;
    private BankAccountDao bankAccountDao;
    private NotifyDao notifyDao;

    @Autowired
    public DebtService(DebtDao debtDao, UserDao userDao, BankAccountDao bankAccountDao, NotifyDao notifyDao) {
        this.debtDao = debtDao;
        this.userDao = userDao;
        this.bankAccountDao = bankAccountDao;
        this.notifyDao = notifyDao;
    }

    public List<Debt> getAll() {
        return debtDao.findAll();
    }

    public Debt getById(int id) throws DebtNotFoundException, NotAuthenticatedClient, UserNotFoundException {
        Debt d = debtDao.find(id);
        if (d == null) {
            throw new DebtNotFoundException(id);
        }
        if (!isCreator(d)) {
            throw new NotAuthenticatedClient();
        }
        return d;
    }

    public boolean persist(Debt debt, int accId) throws UserNotFoundException, BankAccountNotFoundException, NotAuthenticatedClient {
        User u = isLogged();
        BankAccount bankAccount = bankAccountDao.find(accId);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException();
        }
        if (!isUserOwnerOfBankAccount(bankAccount)) {
            throw new NotAuthenticatedClient();
        }
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
    @Scheduled(cron = "0 */12 * * * ")
    public void checkNotifyDates() {
        System.out.println("DEBT");
        //todo new table/entity debt_id creator typeNotification
        //check if exists, maybe every 12 hours notificate check
        //try catch pouzij
        try {
            List<Debt> notifyDebts = debtDao.getNotifyDebts();
            for (Debt notifiedDebt : notifyDebts) {
                Notify notifyEntity = new Notify();
                notifyEntity.setCreator(notifiedDebt.getCreator());
                notifyEntity.setDebt(notifiedDebt);
                notifyEntity.setTypeNotification(TypeNotification.NOTIFY);

                notifyDao.persist(notifyEntity);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Scheduled(cron = "0 */12 * * * ")
    public void checkDeadlineDates() {
        System.out.println("DEADLINE");
        //todo new table/entity debt_id creator typeNotification
        //check if exists, maybe every 12 hours notificate check
        //try catch pouzij
    }

//    @Async
//    public void asyncMethodCheckingDebts() throws UserNotFoundException, InterruptedException, NotAuthenticatedClient {
//        System.out.println("Execute method asynchronously. "
//                + Thread.currentThread().getName());
//        SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
//        User user = isLogged();
//        while (SecurityUtils.getCurrentUser() != null) {
//            List<Debt> debts = user.getMyDebts();
//
//            for (Debt d : debts) {
//                String notifyDate = d.getNotifyDate();
//                String deadlineDate = d.getDeadline();
//                int resultNotifyDate = notifyDate.compareTo(format.format(new Date()));
//                int resultDeadlineDate = deadlineDate.compareTo(format.format(new Date()));
//                System.out.println("NOTIFY: " + resultNotifyDate);
//                System.out.println("NOTIFY: " + resultDeadlineDate);
//
//                if (resultNotifyDate <= 0) {
//                    System.out.println("NOTIFY DEBT!");
//                } else if (resultDeadlineDate <= 0) {
//                    //todo if date >= deadline
//                    System.out.println("DEADLINE DEBT!");
//                }
//            }
//            Thread.sleep(5000);
//        }
//    }

    private boolean validate(Debt debt) {
        if (debt.getName().trim().isEmpty() || debtDao.find(debt.getId()) != null) {
            return false;
        }
        boolean notExist = true;
        List<Debt> debts = getAll();
        for (Debt d : debts) {
            if (d.getName().equals(debt.getName())) {
                notExist = false;
                break;
            }
        }
        return notExist;
    }

    public void remove(int id) throws NotAuthenticatedClient, DebtNotFoundException, UserNotFoundException {
        Debt debt = getById(id);
        debtDao.remove(debt);
    }

    public Debt updateDebt(int id, Debt debt) throws DebtNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        Debt da = getById(id);

        da.setName(debt.getName());
        da.setAmount(debt.getAmount());
        da.setDescription(debt.getDescription());
        da.setDeadline(debt.getDeadline());
        da.setReplay(debt.getReplay());
        da.setNotifyDate(debt.getNotifyDate());

        return debtDao.update(da);
    }

    private boolean isCreator(Debt debt) throws UserNotFoundException, NotAuthenticatedClient {
        User user = isLogged();
        return debt.getCreator().getId() == user.getId();
    }

    private boolean isUserOwnerOfBankAccount(BankAccount bankAccount) throws UserNotFoundException, NotAuthenticatedClient {
        User user = isLogged();
        List<User> owners = bankAccount.getOwners();
        for (User owner : owners) {
            if (owner.getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }

    private User isLogged() throws NotAuthenticatedClient, UserNotFoundException {
        if (SecurityUtils.getCurrentUser() == null) {
            throw new NotAuthenticatedClient();
        }
        User user = userDao.find(SecurityUtils.getCurrentUser().getId());
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }
}
