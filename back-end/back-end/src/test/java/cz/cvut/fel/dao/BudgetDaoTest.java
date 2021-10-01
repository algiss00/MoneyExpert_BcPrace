package cz.cvut.fel.dao;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.model.Budget;
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
public class BudgetDaoTest {
    @Autowired
    private BudgetDao budgetDao;

    @Test
    public void find() {
        Budget budget = budgetDao.find(19);
        assertNotNull(budget);
        assertEquals("Jidlo budget", budget.getName());
        assertEquals(1000, budget.getAmount(), 0.0);
        assertEquals(50, budget.getPercentNotif());
    }

    @Test
    public void persist() {
        Budget generatedBudget = Generator.generateDefaultBudget();
        Budget persistedBudget = budgetDao.persist(generatedBudget);
        assertNotNull(persistedBudget);
        assertEquals(generatedBudget.getName(), persistedBudget.getName());
        assertEquals(generatedBudget.getAmount(), persistedBudget.getAmount(), 0.0);
        assertEquals(generatedBudget.getPercentNotif(), persistedBudget.getPercentNotif());
    }

    @Test
    public void remove() {
        Budget budget = budgetDao.find(20);
        assertNotNull(budget);
        budgetDao.remove(budget);
        assertNull(budgetDao.find(20));
    }

    @Test
    public void update() {
        Budget budget = budgetDao.find(21);
        assertNotNull(budget);
        assertEquals("Byt budget", budget.getName());
        budget.setName("test update name");
        budgetDao.update(budget);
        Budget updatedBudget = budgetDao.find(21);
        assertEquals("test update name", updatedBudget.getName());
    }
}
