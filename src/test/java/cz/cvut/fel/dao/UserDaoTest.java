package cz.cvut.fel.dao;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.model.User;
import generator.Generator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
@Sql("classpath:test-data.sql")
public class UserDaoTest {
    @Autowired
    private UserDao userDao;

    @Test
    public void findUser() {
        User user = userDao.find(10);
        assertNotNull(user);
        assertEquals("john123", user.getUsername());
        assertEquals("emailTest1@ads.cz", user.getEmail());
        assertEquals("Sicker", user.getLastname());
        assertEquals("John", user.getName());
    }

    @Test
    public void findByEmail() {
        User user = userDao.getByEmail("emailTest2@ads.cz");
        assertNotNull(user);
        assertEquals("ali123", user.getUsername());
        assertEquals("Sky", user.getLastname());
        assertEquals("Ali", user.getName());
    }

    @Test
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
    public void remove() {
        User user = userDao.find(10);
        assertNotNull(user);
        userDao.remove(user);
        assertNull(userDao.find(10));
    }

    @Test
    public void update() {
        User user = userDao.find(13);
        assertNotNull(user);
        assertEquals("petr123", user.getUsername());
        user.setUsername("pertUpdateTest");
        userDao.update(user);
        User updatedUser = userDao.find(13);
        assertEquals("pertUpdateTest", updatedUser.getUsername());
    }
}
