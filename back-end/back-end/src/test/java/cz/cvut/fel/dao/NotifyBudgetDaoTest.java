package cz.cvut.fel.dao;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.model.Budget;
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

import java.util.List;

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
    }

    @Test
    public void getBudgetsNotifyBudgets() {
        List<NotifyBudget> notifyBudget = notifyBudgetDao.getNotifyBudgetByBudgetId(19);
        assertFalse(notifyBudget.isEmpty());
        assertEquals(2, notifyBudget.size());

        int[] ids = {25, 26};
        for (int i = 0; i < notifyBudget.size(); i++) {
            assertEquals(ids[i], notifyBudget.get(i).getId());
        }
    }

    @Test
    public void getNotifyBudgetsFromBankAccount() {
        List<NotifyBudget> notifyBudget = notifyBudgetDao.getNotifyBudgetsFromBankAccount(17);
        assertFalse(notifyBudget.isEmpty());
        assertEquals(3, notifyBudget.size());

        int[] ids = {27, 28, 29};
        for (int i = 0; i < notifyBudget.size(); i++) {
            assertEquals(ids[i], notifyBudget.get(i).getId());
        }
    }

    @Test
    public void getNotifyBudgetsFromBankAccountNotFoundAny() {
        List<NotifyBudget> notifyBudget = notifyBudgetDao.getNotifyBudgetsFromBankAccount(16);
        assertTrue(notifyBudget.isEmpty());
    }

    @Test
    public void getBudgetsNotifyBudgetByType() {
        NotifyBudget notifyBudget = notifyBudgetDao.getBudgetsNotifyBudgetByType(19, TypeNotification.BUDGET_AMOUNT);

        assertEquals(25, notifyBudget.getId());
    }

    @Test
    public void alreadyExistsBudget() throws Exception {
        assertTrue(notifyBudgetDao.alreadyExistsBudget(19, TypeNotification.BUDGET_AMOUNT));
        assertFalse(notifyBudgetDao.alreadyExistsBudget(22, TypeNotification.BUDGET_PERCENT));
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
        assertEquals("Jidlo budget", notifyBudget.getBudget().getName());
        notifyBudget.setBudget(generated);
        notifyBudgetDao.update(notifyBudget);
        NotifyBudget updatedNotifyB = notifyBudgetDao.find(26);
        assertEquals(generated.getName(), updatedNotifyB.getBudget().getName());
    }

    @Test
    public void deleteNotifyBudgetByDebtId() throws Exception {
        notifyBudgetDao.deleteNotifyBudgetByBudgetId(19);
        NotifyBudget notifyBudget = notifyBudgetDao.find(25);
        assertNull(notifyBudget);
        NotifyBudget notifyBudget2 = notifyBudgetDao.find(26);
        assertNull(notifyBudget2);
    }
}
