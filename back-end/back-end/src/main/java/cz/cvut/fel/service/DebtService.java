package cz.cvut.fel.service;

import cz.cvut.fel.dao.BankAccountDao;
import cz.cvut.fel.dao.DebtDao;
import cz.cvut.fel.dao.UserDao;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Category;
import cz.cvut.fel.model.Debt;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.BankAccountNotFoundException;
import cz.cvut.fel.service.exceptions.DebtNotFoundException;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DebtService {
    private DebtDao debtDao;
    private UserDao userDao;
    private BankAccountDao bankAccountDao;

    @Autowired
    public DebtService(DebtDao debtDao, UserDao userDao, BankAccountDao bankAccountDao) {
        this.debtDao = debtDao;
        this.userDao = userDao;
        this.bankAccountDao = bankAccountDao;
    }

    public List<Debt> getAll() {
        return debtDao.findAll();
    }

    public List<Debt> getAllAccountsDebts(int accId) throws BankAccountNotFoundException {
        if (bankAccountDao.find(accId) == null) {
            throw new BankAccountNotFoundException();
        }
        return debtDao.getAllAccountsDebts(accId);
    }

    public List<Debt> getAllUsersDebts(int uid) {
        return debtDao.getAllUsersDebts(uid);
    }


    public Debt getById(int id) throws DebtNotFoundException {
        Debt d = debtDao.find(id);
        if (d == null) {
            throw new DebtNotFoundException(id);
        }
        return d;
    }
    
    public boolean persist(Debt debt, int uid, int accId) throws UserNotFoundException, BankAccountNotFoundException {
        User u = userDao.find(uid);
        if (u == null) {
            throw new UserNotFoundException(uid);
        }
        BankAccount bankAccount = bankAccountDao.find(accId);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException();
        }
        if (debt == null)
            throw new NullPointerException("debt can not be Null.");
        if (!validate(debt))
            return false;
        debt.setCreator(u);
        debt.setBankAccount(bankAccount);
        debtDao.persist(debt);
        return true;
    }

    public boolean validate(Debt debt) {
        return !debt.getName().trim().isEmpty();
    }

    public void remove(int id) throws NotAuthenticatedClient, DebtNotFoundException {
        Debt debt = getById(id);
        if (!isCreator(SecurityUtils.getCurrentUser(), debt)) {
            throw new NotAuthenticatedClient();
        }
        debtDao.remove(debt);
    }

//    public boolean alreadyExists(Debt debt) {
//        return debtDao.find(debt.getId()) != null;
//    }

    public Debt updateDebt(int id, Debt debt) throws DebtNotFoundException {
        Debt da = getById(id);

        da.setName(debt.getName());
        da.setAmount(debt.getAmount());
        da.setDescription(debt.getDescription());
        da.setEndDate(debt.getEndDate());
        da.setReplay(debt.getReplay());
        da.setStartDate(debt.getStartDate());

        return debtDao.update(da);
    }

    private boolean isCreator(User user, Debt debt) {
        return debt.getCreator() == user;
    }
}
