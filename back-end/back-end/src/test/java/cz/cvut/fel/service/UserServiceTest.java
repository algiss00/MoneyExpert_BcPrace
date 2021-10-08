package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.UserDao;
import cz.cvut.fel.model.User;
import generator.Generator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

//@RunWith(SpringRunner.class)
//@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
//public class UserServiceTest {
//
//    private UserService userService;
//    @Mock
//    private UserDao userDao;
//
//    @Before
//    public void setUp() {
//        userService = new UserService(userDao);
//    }
//
//    @Test
//    public void registrationUserAndCheckDefaultCategories() throws Exception {
//        User user = Generator.generateDefaultUser();
//        User persistedEntity = userService.persist(user);
//        assertNotNull(persistedEntity);
//        assertEquals(user.getUsername(), userService.getByIdUser(persistedEntity.getId()).getUsername());
//        assertEquals(11, persistedEntity.getMyCategories().size());
//    }
//
//}
