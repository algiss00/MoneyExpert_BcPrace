package cz.cvut.fel.service;

import cz.cvut.fel.dao.UserDao;
import cz.cvut.fel.model.User;
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
        for (User user : userDao.findAll()) {
            if (user.getUsername().equals(username))
                return user;
        }
        return null;
    }

    public User getByEmail(String email) {
        for (User user : userDao.findAll()) {
            if (user.getEmail().equals(email))
                return user;
        }
        return null;
    }

    public boolean alreadyExists(int id) {
        return userDao.find(id) != null;
    }

    public boolean emailExist(String email) {
        if(getByEmail(email) != null){
            return false;
        }
        return true;
    }

    public List<User> getAll() {
        return userDao.findAll();
    }

    public boolean persist(User user) {
        if (user == null)
            throw new NullPointerException("User can not be Null.");
        if (alreadyExists(user.getId()) || !emailExist(user.getEmail()))
            return false;
        userDao.persist(user);
        return true;
    }
}
