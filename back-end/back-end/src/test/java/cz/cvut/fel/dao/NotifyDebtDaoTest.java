package cz.cvut.fel.dao;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.model.Debt;
import cz.cvut.fel.model.NotifyBudget;
import cz.cvut.fel.model.NotifyDebt;
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

import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
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
    public void getUsersNotifyBudgets() {
        List<NotifyDebt> notifyDebts = notifyDebtDao.getUsersNotifyDebts(13);
        assertFalse(notifyDebts.isEmpty());
        assertEquals(3, notifyDebts.size());

        int[] ids = {28, 30, 31};
        for (int i = 0; i < notifyDebts.size(); i++) {
            assertEquals(ids[i], notifyDebts.get(i).getId());
        }
    }

    @Test
    public void getUsersNotifyBudgetsByType() {
        List<NotifyDebt> notifyDebts = notifyDebtDao.getUsersNotifyDebtsByType(13, TypeNotification.DEBT_NOTIFY);
        assertFalse(notifyDebts.isEmpty());
        assertEquals(2, notifyDebts.size());

        int[] ids = {28, 30};
        for (int i = 0; i < notifyDebts.size(); i++) {
            assertEquals(ids[i], notifyDebts.get(i).getId());
        }
    }

    @Test
    public void alreadyExistsBudget() throws Exception {
        assertNotNull(notifyDebtDao.alreadyExistsDebt(22, TypeNotification.DEBT_NOTIFY));
        assertNull(notifyDebtDao.alreadyExistsDebt(26, TypeNotification.DEBT_NOTIFY));
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
