package cz.cvut.fel.service;

import cz.cvut.fel.dao.DebtDao;
import cz.cvut.fel.model.Debt;
import cz.cvut.fel.model.User;
import cz.cvut.fel.service.exceptions.BankAccountNotFoundException;
import cz.cvut.fel.service.exceptions.DebtNotFoundException;
import cz.cvut.fel.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DebtService {
    private DebtDao debtDao;
    private UserService userService;
    private BankAccountService bankAccountService;

    @Autowired
    public DebtService(DebtDao debtDao, UserService userService, BankAccountService bankAccountService) {
        this.debtDao = debtDao;
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    public List<Debt> getAll() {
        return debtDao.findAll();
    }

    public List<Debt> getAllAccountsDebts(int accId) throws BankAccountNotFoundException {
        bankAccountService.getById(accId);
        return debtDao.getAllAccountsDebts(accId);
    }

    public List<Debt> getAllUsersDebts(int uid) {
        return debtDao.getAllUsersDebts(uid);
    }


    public Debt getById(int id) throws DebtNotFoundException {
        Debt u = debtDao.find(id);
        if (u == null) {
            throw new DebtNotFoundException(id);
        }
        return u;
    }

    //todo debt add to Account
    public boolean persist(Debt debt, int uid) throws UserNotFoundException {
        User u = userService.getById(uid);
        if (debt == null)
            throw new NullPointerException("debt can not be Null.");
        if (!validate(debt))
            return false;
        debt.setCreator(u);
        debtDao.persist(debt);
        return true;
    }

    public boolean validate(Debt debt) {
        return !debt.getName().trim().isEmpty();
    }

    public void remove(int id) throws DebtNotFoundException {
        Debt debt = getById(id);
        debt.setCreator(null);
        debt.setBankAccount(null);
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
}
