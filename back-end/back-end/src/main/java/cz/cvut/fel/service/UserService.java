package cz.cvut.fel.service;

import cz.cvut.fel.dao.UserDao;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return userDao.find(user.getId()) != null || getByUsername(user.getUsername()) != null
                || getByEmail(user.getEmail()) != null;
    }

    public boolean emailExist(String email) {
        return getByEmail(email) == null;
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

    public List<BankAccount> getAvailableAccounts(int id) throws UserNotFoundException {
        User u = getById(id);
        return u.getAvailableBankAccounts();
    }

    public boolean persist(User user) {
        if (user == null)
            throw new NullPointerException("User can not be Null.");
        if (alreadyExists(user) || !emailExist(user.getEmail()))
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
        if (getByUsername(username) != null) {
            throw new Exception("Username is taken");
        }
        u.setUsername(username);
        return userDao.update(u);
    }

    public User updateEmail(String email, int id) throws Exception {
        User u = getById(id);
        if (getByEmail(email) != null) {
            throw new Exception("Email is taken");
        }
        u.setEmail(email);
        return userDao.update(u);
    }

    public void remove(int id) throws UserNotFoundException, NotAuthenticatedClient {
        if (SecurityUtils.getCurrentUser() == null) {
            throw new NotAuthenticatedClient();
        }
        User user = getById(id);
        user.getAvailableBankAccounts().clear();
        userDao.remove(user);
    }
}
