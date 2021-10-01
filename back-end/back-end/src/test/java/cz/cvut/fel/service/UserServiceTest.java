package cz.cvut.fel.service;

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
@Sql("classpath:test-data.sql")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void registrationUserAndCheckDefaultCategories() throws Exception {
        User user = Generator.generateDefaultUser();
        User persistedEntity = userService.persist(user);
        assertNotNull(persistedEntity);
        assertEquals(user.getUsername(), userService.getByIdUser(persistedEntity.getId()).getUsername());
        assertEquals(11, persistedEntity.getMyCategories().size());
    }

}
