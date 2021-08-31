package cz.cvut.fel.service;

import cz.cvut.fel.dao.UserDao;
import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserService {
    private UserDao userDao;

    @Autowired
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
                || getByEmail(user.getEmail()) != null;
    }

    public List<User> getAll() {
        return userDao.findAll();
    }

    public User getById(int id) throws UserNotFoundException, NotAuthenticatedClient {
        User u = userDao.find(id);
        if (u == null) {
            throw new UserNotFoundException(id);
        }
        if (!isLogged()) {
            throw new NotAuthenticatedClient();
        }
        return u;
    }

    public List<BankAccount> getAvailableAccounts(int id) throws UserNotFoundException, NotAuthenticatedClient {
        User u = getById(id);
        return u.getAvailableBankAccounts();
    }

    public List<Budget> getAllUsersBudgets(int uid) throws UserNotFoundException, NotAuthenticatedClient {
        User u = getById(uid);
        return u.getMyBudgets();
    }

    public List<Category> getAllUsersCategories(int uid) throws UserNotFoundException, NotAuthenticatedClient {
        User u = getById(uid);
        return u.getMyCategories();
    }

    public List<Debt> getAllUsersDebts(int uid) throws UserNotFoundException, NotAuthenticatedClient {
        User u = getById(uid);
        return u.getMyDebts();
    }

    public boolean persist(User user) {
        Objects.requireNonNull(user);
        if (alreadyExists(user))
            return false;
        userDao.persist(user);
        return true;
    }

    public User updateUser(int id, User user) throws Exception {
        User u = getById(id);
        if (alreadyExists(u)) {
            throw new Exception("User is already exists");
        }
        u.setEmail(user.getEmail());
        u.setUsername(user.getUsername());
        u.setName(user.getName());
        u.setLastname(user.getLastname());
        return userDao.update(u);

    }

    public User updateUsername(String username, int id) throws Exception {
        User u = getById(id);
        if (alreadyExists(u)) {
            throw new Exception("Username is taken");
        }
        u.setUsername(username);
        return userDao.update(u);
    }

    public User updateEmail(String email, int id) throws Exception {
        User u = getById(id);
        if (alreadyExists(u)) {
            throw new Exception("Email is taken");
        }
        u.setEmail(email);
        return userDao.update(u);
    }

    public void remove(int id) throws UserNotFoundException, NotAuthenticatedClient {
        if (!isLogged()) {
            throw new NotAuthenticatedClient();
        }
        User user = getById(id);
        user.getAvailableBankAccounts().clear();
        user.getMyBudgets().clear();
        user.getMyCategories().clear();
        user.getMyDebts().clear();
        userDao.remove(user);
    }

    private boolean isLogged() {
        return SecurityUtils.getCurrentUser() != null;
    }
}
