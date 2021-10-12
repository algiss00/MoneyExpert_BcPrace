package cz.cvut.fel.service;

import cz.cvut.fel.dao.UserDao;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class HelperFunctions {

    public static void authUser(MockedStatic<SecurityUtils> utilities, UserDao userDao, User user) {
        utilities.when(SecurityUtils::getCurrentUser).thenReturn(user);
        assertEquals(user, SecurityUtils.getCurrentUser());
        when(userDao.find(anyInt())).thenReturn(user);
    }
}
