package cz.cvut.fel.dao;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.NotifyBudget;
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
public class NotifyBudgetDaoTest {
    @Autowired
    private NotifyBudgetDao notifyBudgetDao;

    @Test
    public void find() {
        NotifyBudget notifyBudget = notifyBudgetDao.find(25);
        assertNotNull(notifyBudget);
        assertEquals("Jidlo budget", notifyBudget.getBudget().getName());
        assertEquals("john123", notifyBudget.getCreator().getUsername());
    }

    @Test
    public void persist() {
        NotifyBudget generatedNotifyBudget = Generator.generateDefaultBudgetNotify();
        NotifyBudget persistedNotifyBudget = notifyBudgetDao.persist(generatedNotifyBudget);
        assertNotNull(persistedNotifyBudget);
        assertEquals(generatedNotifyBudget.getBudget().getName(), persistedNotifyBudget.getBudget().getName());
    }

    @Test
    public void remove() {
        NotifyBudget notifyBudget = notifyBudgetDao.find(25);
        assertNotNull(notifyBudget);
        notifyBudgetDao.remove(notifyBudget);
        assertNull(notifyBudgetDao.find(25));
    }

    @Test
    public void update() {
        Budget generated = Generator.generateDefaultBudget();
        NotifyBudget notifyBudget = notifyBudgetDao.find(26);
        assertNotNull(notifyBudget);
        assertEquals("Auto budget", notifyBudget.getBudget().getName());
        notifyBudget.setBudget(generated);
        notifyBudgetDao.update(notifyBudget);
        NotifyBudget updatedNotifyB = notifyBudgetDao.find(26);
        assertEquals(generated.getName(), updatedNotifyB.getBudget().getName());
    }
}
