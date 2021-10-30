package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.NotValidDataException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserService extends AbstractServiceHelper {
    private final PasswordEncoder passwordEncoder;

    public UserService(UserDao userDao, BankAccountDao bankAccountDao, TransactionDao transactionDao,
                       BudgetDao budgetDao, DebtDao debtDao, CategoryDao categoryDao,
                       NotifyBudgetDao notifyBudgetDao, NotifyDebtDao notifyDebtDao, PasswordEncoder passwordEncoder) {
        super(userDao, bankAccountDao, transactionDao, budgetDao, debtDao, categoryDao, notifyBudgetDao, notifyDebtDao);
        this.passwordEncoder = passwordEncoder;
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
        User u = getAuthenticatedUser();
        return u.getAvailableBankAccounts();
    }

    public List<Budget> getAllUsersBudgets() throws NotAuthenticatedClient {
        User u = getAuthenticatedUser();
        return u.getMyBudgets();
    }

    public List<Category> getAllUsersCategories() throws Exception {
        return categoryDao.getAllUsersCategory(getAuthenticatedUser().getId());
    }

    public List<Debt> getAllUsersDebts() throws NotAuthenticatedClient {
        return debtDao.getUsersDebt(getAuthenticatedUser().getId());
    }

    public User persist(User userObj) throws Exception {
        Objects.requireNonNull(userObj);
        if (alreadyExists(userObj))
            throw new NotValidDataException("user");
        User user = new User();
        user.setMyCategories(getDefaultCategories());
        user.setEmail(userObj.getEmail());
        user.setUsername(userObj.getUsername());
        user.setLastname(userObj.getLastname());
        user.setName(userObj.getName());
        user.setPassword(passwordEncoder.encode(userObj.getPassword()));

        return userDao.persist(user);
    }

    public User updateUserBasic(User user) throws Exception {
        User u = getAuthenticatedUser();
        u.setName(user.getName());
        u.setLastname(user.getLastname());
        return userDao.update(u);

    }

    public User updateUsername(String username) throws Exception {
        User u = getAuthenticatedUser();
        if (getByUsername(username) != null) {
            throw new Exception("Username is taken");
        }
        u.setUsername(username);
        return userDao.update(u);
    }

    public User updateEmail(String email) throws Exception {
        User u = getAuthenticatedUser();
        if (getByEmail(email) != null) {
            throw new Exception("Email is taken");
        }
        u.setEmail(email);
        return userDao.update(u);
    }

    /**
     * First delete debts and budgets in their is deleted all Notifies entities,
     * after delete all relation with category then delete all user-created categories,
     * after delete all users bankAccounts
     *
     * @throws Exception
     */
    public void remove() throws Exception {
        User user = getAuthenticatedUser();

        for (Budget myBudget : user.getMyBudgets()) {
            removeBudget(myBudget.getId());
        }

        for (Debt myDebt : user.getMyDebts()) {
            removeDebt(myDebt.getId());
        }

        for (Category myCategory : user.getMyCategories()) {
            // delete all relations
            categoryDao.deleteUsersRelationCategoryById(user.getId(), myCategory.getId());
        }

        for (Category category : categoryDao.getUsersCreatedCategory(user.getId())) {
            categoryDao.remove(category);
        }

        // todo - u dont know if user is not just a guest, he not able to delete account
        for (BankAccount availableBankAccount : user.getAvailableBankAccounts()) {
            removeBankAcc(availableBankAccount.getId());
        }

        userDao.remove(user);
    }
}
