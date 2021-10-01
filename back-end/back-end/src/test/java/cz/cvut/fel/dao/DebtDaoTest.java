package cz.cvut.fel.dao;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.model.Debt;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
@Sql("classpath:test-data.sql")
public class DebtDaoTest {
    @Autowired
    private DebtDao debtDao;

    @Test
    public void find() throws ParseException {
        Debt debt = debtDao.find(22);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = formatter.parse("2021-10-10");
        Date notifyDate = formatter.parse("2021-10-09");

        assertNotNull(debt);
        assertEquals("Honza dluh", debt.getName());
        assertEquals("Debt to Honza", debt.getDescription());
        assertEquals(1000, debt.getAmount(), 0.0);
        assertEquals(deadline, formatter.parse(debt.getDeadline().toString()));
        assertEquals(notifyDate, formatter.parse(debt.getNotifyDate().toString()));
    }

    @Test
    public void persist() throws ParseException {
        Debt generatedDebt = Generator.generateDefaultDebt();
        Debt persistedDebt = debtDao.persist(generatedDebt);

        assertNotNull(persistedDebt);
        assertEquals(generatedDebt.getName(), persistedDebt.getName());
        assertEquals(generatedDebt.getDescription(), persistedDebt.getDescription());
        assertEquals(generatedDebt.getAmount(), persistedDebt.getAmount(), 0.0);
        assertEquals(generatedDebt.getDeadline(), persistedDebt.getDeadline());
        assertEquals(generatedDebt.getNotifyDate(), persistedDebt.getNotifyDate());
    }

    @Test
    public void remove() {
        Debt debt = debtDao.find(23);
        assertNotNull(debt);
        debtDao.remove(debt);
        assertNull(debtDao.find(23));
    }

    @Test
    public void update() {
        Debt debt = debtDao.find(24);
        assertNotNull(debt);
        assertEquals("Karl dluh", debt.getName());
        debt.setName("test update name");
        debtDao.update(debt);
        Debt updatedDebt = debtDao.find(24);
        assertEquals("test update name", updatedDebt.getName());
    }
}
