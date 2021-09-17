package cz.cvut.fel.service;

import cz.cvut.fel.dao.UserDao;
import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User getByUsername(String username) {
        return userDao.getByUsername(username);
    }

    public User getByEmail(String email) {
        return userDao.getByEmail(email);
    }

    public boolean alreadyExists(User user) {
        return getByUsername(user.getUsername()) != null
                || getByEmail(user.getEmail()) != null || userDao.find(user.getId()) != null;
    }

    public List<User> getAll() {
        return userDao.findAll();
    }

    public User getById(int id) throws UserNotFoundException {
        User u = userDao.find(id);
        if (u == null) {
            throw new UserNotFoundException(id);
        }
        return u;
    }

    public List<BankAccount> getAvailableAccounts() throws NotAuthenticatedClient {
        User u = isLogged();
        return u.getAvailableBankAccounts();
    }

    public List<Budget> getAllUsersBudgets() throws NotAuthenticatedClient {
        User u = isLogged();
        return u.getMyBudgets();
    }

    public List<Category> getAllUsersCategories() throws NotAuthenticatedClient {
        User u = isLogged();
        return u.getMyCategories();
    }

    public List<Debt> getAllUsersDebts() throws NotAuthenticatedClient {
        User u = isLogged();
        return u.getMyDebts();
    }

    public boolean persist(User user) {
        Objects.requireNonNull(user);
        if (alreadyExists(user))
            return false;
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

    public User isLogged() throws NotAuthenticatedClient {
        User user;
        try {
            user = userDao.find(SecurityUtils.getCurrentUser().getId());
            if (user == null) {
                throw new UserNotFoundException();
            }
        } catch (Exception ex) {
            throw new NotAuthenticatedClient();
        }
        return user;
    }
}
