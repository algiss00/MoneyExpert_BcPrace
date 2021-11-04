package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.NotValidDataException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<BankAccount> getAvailableBankAccounts() throws NotAuthenticatedClient {
        User u = getAuthenticatedUser();
        return u.getAvailableBankAccounts();
    }

    public List<BankAccount> getCreatedBankAccounts() throws NotAuthenticatedClient {
        User u = getAuthenticatedUser();
        return u.getCreatedBankAccounts();
    }

    public List<BankAccount> getAllUsersBankAccounts() throws Exception {
        List<BankAccount> availableBankAccounts = getAvailableBankAccounts();
        List<BankAccount> createdBankAcc = getCreatedBankAccounts();

        return Stream.concat(availableBankAccounts.stream(), createdBankAcc.stream())
                .collect(Collectors.toList());
    }

    public List<Category> getAllUsersCategories() throws Exception {
        return categoryDao.getAllUsersCategory(getAuthenticatedUser().getId());
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

    public void updatePassword(String oldPassword, String newPassword) throws Exception {
        User user = getAuthenticatedUser();
        if (oldPassword.equals(newPassword)) {
            throw new NotValidDataException();
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new BadCredentialsException("Bad Credentials.");

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    /**
     * first delete all relation with category then delete all user-created categories,
     * after delete all users bankAccounts
     *
     * @throws Exception
     */
    public void remove() throws Exception {
        User user = getAuthenticatedUser();
        List<BankAccount> createdBankAccounts = user.getCreatedBankAccounts();
        List<Category> createdCategories = categoryDao.getUsersCreatedCategory(user.getId());


        for (Category category : createdCategories) {
            // delete created categories
            removeCategory(category.getId());
        }

        for (Category myCategory : user.getMyCategories()) {
            // delete all relations with categories
            categoryDao.deleteUsersRelationCategoryById(user.getId(), myCategory.getId());
        }

        // delete all created by User BankAccounts
        for (BankAccount createdBankAcc : createdBankAccounts) {
            removeBankAcc(createdBankAcc.getId());
        }

        userDao.remove(user);
    }
}
