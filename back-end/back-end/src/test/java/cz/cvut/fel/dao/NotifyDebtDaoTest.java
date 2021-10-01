package cz.cvut.fel.dao;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.Debt;
import cz.cvut.fel.model.NotifyBudget;
import cz.cvut.fel.model.NotifyDebt;
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

import java.text.ParseException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
@Sql("classpath:test-data.sql")
public class NotifyDebtDaoTest {
    @Autowired
    private NotifyDebtDao notifyDebtDao;

    @Test
    public void find() {
        NotifyDebt notifyDebt = notifyDebtDao.find(28);
        assertNotNull(notifyDebt);
        assertEquals("Honza dluh", notifyDebt.getDebt().getName());
    }

    @Test
    public void persist() throws ParseException {
        NotifyDebt generatedNotifyDebt = Generator.generateDefaultDebtNotify();
        NotifyDebt persistedNotifyDebt = notifyDebtDao.persist(generatedNotifyDebt);
        assertNotNull(persistedNotifyDebt);
        assertEquals(generatedNotifyDebt.getDebt().getName(), persistedNotifyDebt.getDebt().getName());
    }

    @Test
    public void remove() {
        NotifyDebt notifyDebt = notifyDebtDao.find(29);
        assertNotNull(notifyDebt);
        notifyDebtDao.remove(notifyDebt);
        assertNull(notifyDebtDao.find(29));
    }

    @Test
    public void update() throws ParseException {
        Debt generated = Generator.generateDefaultDebt();
        NotifyDebt notifyDebt = notifyDebtDao.find(28);
        assertNotNull(notifyDebt);
        assertEquals("Honza dluh", notifyDebt.getDebt().getName());
        notifyDebt.setDebt(generated);
        notifyDebtDao.update(notifyDebt);
        NotifyDebt updatedNotifyDebt = notifyDebtDao.find(28);
        assertEquals(generated.getName(), updatedNotifyDebt.getDebt().getName());
    }
}
