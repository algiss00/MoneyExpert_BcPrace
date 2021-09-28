package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserService extends AbstractServiceHelper {

    //private PasswordEncoder passwordEncoder;

    public UserService(UserDao userDao, BankAccountDao bankAccountDao, TransactionDao transactionDao,
                       BudgetDao budgetDao, DebtDao debtDao, CategoryDao categoryDao,
                       NotifyBudgetDao notifyBudgetDao, NotifyDebtDao notifyDebtDao) {
        super(userDao, bankAccountDao, transactionDao, budgetDao, debtDao, categoryDao, notifyBudgetDao, notifyDebtDao);
    }

    public User getByUsername(String username) {
        return userDao.getByUsername(username);
    }

    public User getByEmail(String email) throws Exception {
        return userDao.getByEmail(email);
    }

    public boolean alreadyExists(User user) throws Exception {
        return getByUsername(user.getUsername()) != null
                || getByEmail(user.getEmail()) != null;
    }

    public List<User> getAll() {
        return userDao.findAll();
    }

    public List<BankAccount> getAvailableAccounts() throws NotAuthenticatedClient {
        User u = isLogged();
        return u.getAvailableBankAccounts();
    }

    public List<Budget> getAllUsersBudgets() throws NotAuthenticatedClient {
        User u = isLogged();
        return u.getMyBudgets();
    }

    public List<Category> getAllUsersCategories() throws Exception {
        return categoryDao.getUsersCategory(isLogged().getId());
    }

    public List<Debt> getAllUsersDebts() throws NotAuthenticatedClient {
        return debtDao.getUsersDebt(isLogged().getId());
    }

    public boolean persist(User userObj) throws Exception {
        Objects.requireNonNull(userObj);
        if (alreadyExists(userObj))
            return false;
        User user = new User();
        user.setMyCategories(getDefaultCategories());
        user.setEmail(userObj.getEmail());
        user.setUsername(userObj.getUsername());
        user.setLastname(userObj.getLastname());
        user.setName(userObj.getName());
        // todo encode password
        user.setPassword(userObj.getPassword());

        userDao.persist(user);
        return true;
    }

    public User updateUserBasic(User user) throws Exception {
        User u = isLogged();
        u.setName(user.getName());
        u.setLastname(user.getLastname());
        return userDao.update(u);

    }

    public User updateUsername(String username) throws Exception {
        User u = isLogged();
        if (getByUsername(username) != null) {
            throw new Exception("Username is taken");
        }
        u.setUsername(username);
        return userDao.update(u);
    }

    public User updateEmail(String email) throws Exception {
        User u = isLogged();
        if (getByEmail(email) != null) {
            throw new Exception("Email is taken");
        }
        u.setEmail(email);
        return userDao.update(u);
    }

    public void remove() throws NotAuthenticatedClient {
        User user = isLogged();
        user.getAvailableBankAccounts().clear();
        user.getMyBudgets().clear();
        user.getMyCategories().clear();
        user.getMyDebts().clear();
        userDao.remove(user);
    }
}
