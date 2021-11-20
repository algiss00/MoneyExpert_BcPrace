package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.NotValidDataException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public User getByEmail(String email) {
        return userDao.getByEmail(email);
    }

    public boolean alreadyExists(User user) {
        return getByUsername(user.getUsername()) != null
                || getByEmail(user.getEmail()) != null;
    }

    public List<User> getAll() {
        return userDao.findAll();
    }

    public List<BankAccount> getAvailableBankAccounts() throws NotAuthenticatedClient {
        User u = getAuthenticatedUser();
        return u.getAvailableBankAccounts();
    }

    public List<BankAccount> getCreatedBankAccounts() throws NotAuthenticatedClient {
        User u = getAuthenticatedUser();
        return u.getCreatedBankAccounts();
    }

    /**
     * Get all users BankAccounts
     * First get available BankAccounts
     * Second get created BankAccounts
     * After union this two lists
     *
     * @return
     * @throws Exception
     */
    public List<BankAccount> getAllUsersBankAccounts() throws Exception {
        List<BankAccount> availableBankAccounts = getAvailableBankAccounts();
        List<BankAccount> createdBankAcc = getCreatedBankAccounts();

        return Stream.concat(availableBankAccounts.stream(), createdBankAcc.stream())
                .collect(Collectors.toList());
    }

    public List<Category> getAllUsersCategories() throws Exception {
        return categoryDao.getAllUsersCategory(getAuthenticatedUser().getId());
    }

    /**
     * persist user
     * password je zahashovan
     *
     * @param userObj
     * @return
     * @throws Exception
     */
    public User persist(User userObj) throws Exception {
        Objects.requireNonNull(userObj);
        if (alreadyExists(userObj))
            throw new NotValidDataException("user exists!");
        if (!validate(userObj)) {
            throw new NotValidDataException("Not valid data");
        }
        User user = new User();
        user.setMyCategories(getDefaultCategories());
        user.setEmail(userObj.getEmail());
        user.setUsername(userObj.getUsername());
        user.setLastname(userObj.getLastname());
        user.setName(userObj.getName());
        user.setPassword(passwordEncoder.encode(userObj.getPassword()));

        return userDao.persist(user);
    }

    /**
     * Validate if columns are empty
     *
     * @param user
     * @return
     */
    private boolean validate(User user) {
        return !user.getUsername().trim().isEmpty() && !user.getPassword().trim().isEmpty() &&
                !user.getName().trim().isEmpty() && !user.getEmail().trim().isEmpty() &&
                !user.getLastname().trim().isEmpty();
    }

    /**
     * update only name and lastname
     *
     * @param user
     * @return
     * @throws Exception
     */
    public User updateUserBasic(User user) throws Exception {
        User u = getAuthenticatedUser();
        u.setName(user.getName());
        u.setLastname(user.getLastname());
        return userDao.update(u);

    }

    /**
     * update only Username
     *
     * @param username
     * @return
     * @throws Exception
     */
    public User updateUsername(String username) throws Exception {
        User u = getAuthenticatedUser();
        if (getByUsername(username) != null) {
            throw new Exception("Username is taken");
        }
        u.setUsername(username);
        return userDao.update(u);
    }

    /**
     * update only Email
     *
     * @param email
     * @return
     * @throws Exception
     */
    public User updateEmail(String email) throws Exception {
        User u = getAuthenticatedUser();
        if (getByEmail(email) != null) {
            throw new Exception("Email is taken");
        }
        u.setEmail(email);
        return userDao.update(u);
    }

    /**
     * update only Password
     *
     * @param oldPassword
     * @param newPassword
     * @throws Exception
     */
    public void updatePassword(String oldPassword, String newPassword) throws Exception {
        User user = getAuthenticatedUser();
        if (oldPassword.equals(newPassword)) {
            throw new NotValidDataException();
        }

        // kontrola pokud user vi aktualni password
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new BadCredentialsException("Bad Credentials.");

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    /**
     * Delete User
     * first delete all relation with category then delete all user-created categories,
     * after delete all users bankAccounts
     *
     * @throws Exception
     */
    public void remove() throws Exception {
        User user = getAuthenticatedUser();
        List<BankAccount> availableBankAccounts = user.getAvailableBankAccounts();
        List<Category> createdCategories = categoryDao.getUsersCreatedCategory(user.getId());

        for (Category category : createdCategories) {
            // delete created categories
            removeCategory(category.getId());
        }

        // delete all relations with available BankAccounts
        for (BankAccount available : availableBankAccounts) {
            bankAccountDao.deleteRelationBankAcc(user.getId(), available.getId());
        }

        user.setAvailableBankAccounts(Collections.emptyList());

        userDao.remove(user);
    }
}
