package cz.cvut.fel.dao;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.model.User;
import generator.Generator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
public class userDaoTest {
    @Autowired
    private UserDao userDao;

    @Test
    @Sql("classpath:test-data.sql")
    public void findUser() {
        User user = userDao.find(10);
        assertNotNull(user);
        assertEquals("john123", user.getUsername());
        assertEquals("emailTest1@ads.cz", user.getEmail());
        assertEquals("Sicker", user.getLastname());
        assertEquals("John", user.getName());
    }

    @Test
    @Sql("classpath:test-data.sql")
    public void findByEmail() throws Exception {
        User user = userDao.getByEmail("emailTest2@ads.cz");
        assertNotNull(user);
        assertEquals("ali123", user.getUsername());
        assertEquals("Sky", user.getLastname());
        assertEquals("Ali", user.getName());
    }

    @Test
    @Sql("classpath:test-data.sql")
    public void persist() {
        User generatedUser = Generator.generateDefaultUser();
        User user = userDao.persist(generatedUser);
        assertNotNull(user);
        assertEquals(generatedUser.getUsername(), user.getUsername());
        assertEquals(generatedUser.getEmail(), user.getEmail());
        assertEquals(generatedUser.getLastname(), user.getLastname());
        assertEquals(generatedUser.getName(), user.getName());
    }

    @Test
    @Sql("classpath:test-data.sql")
    public void remove() {
        User user = userDao.find(10);
        assertNotNull(user);
        userDao.remove(user);
        assertNull(userDao.find(10));
    }

//    @Test
//    @Sql("classpath:test-data.sql")
//    public void update() {
//
//    }
}
