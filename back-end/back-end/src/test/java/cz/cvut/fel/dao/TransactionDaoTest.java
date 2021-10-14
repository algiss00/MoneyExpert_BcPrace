package cz.cvut.fel.dao;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dto.TypeTransaction;
import cz.cvut.fel.model.Transaction;
import generator.Generator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
@Sql("classpath:test-data.sql")
public class TransactionDaoTest {
    @Autowired
    private TransactionDao transactionDao;

    @Test
    public void find() throws ParseException {
        Transaction transaction = transactionDao.find(30);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse("2021-10-01");

        assertNotNull(transaction);
        assertEquals(100, transaction.getAmount(), 0.0);
        assertEquals(TypeTransaction.EXPENSE, transaction.getTypeTransaction());
        assertEquals("jottings test 1", transaction.getJottings());
        assertEquals(date, transaction.getDate());
        assertEquals(15, transaction.getBankAccount().getId());
        assertEquals(-4, transaction.getCategory().getId());
    }

    @Test
    public void persist() throws ParseException {
        Transaction generatedTransaction = Generator.generateDefaultTransaction();
        Transaction persistedTransaction = transactionDao.persist(generatedTransaction);

        assertNotNull(persistedTransaction);
        assertEquals(generatedTransaction.getAmount(), persistedTransaction.getAmount(), 0.0);
        assertEquals(generatedTransaction.getTypeTransaction(), persistedTransaction.getTypeTransaction());
        assertEquals(generatedTransaction.getJottings(), persistedTransaction.getJottings());
        assertEquals(generatedTransaction.getDate(), persistedTransaction.getDate());
        assertNull(persistedTransaction.getBankAccount());
        assertNull(persistedTransaction.getCategory());
    }

    @Test
    public void remove() {
        Transaction transaction = transactionDao.find(31);
        assertNotNull(transaction);
        transactionDao.remove(transaction);
        assertNull(transactionDao.find(31));
    }

    @Test
    public void getByMonthSorted() throws ParseException {
        List<Transaction> transactions = transactionDao.getByMonthSorted(9, 2021, 18);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse("2021-09-01");

        assertFalse(transactions.isEmpty());
        assertEquals(3, transactions.size());
        int[] ids = {35, 34, 33};
        for (int i = 0; i < transactions.size(); i++) {
            assertEquals(ids[i], transactions.get(i).getId());
        }
    }

    @Test
    public void getByMonthSortedEmptyMonth() {
        List<Transaction> transactions = transactionDao.getByMonthSorted(5, 2021, 18);
        assertTrue(transactions.isEmpty());
    }

    @Test
    public void getByMonthSortedEmptyByYear() {
        List<Transaction> transactions = transactionDao.getByMonthSorted(9, 2020, 18);
        assertTrue(transactions.isEmpty());
    }

    @Test
    public void getExpenseSum() throws Exception {
        double sum = transactionDao.getExpenseSum(9, 2021, 18);
        assertEquals(300, sum);
    }

    @Test
    public void getExpenseSumException() {
        assertThrows(Exception.class, () -> transactionDao.getExpenseSum(9, 2020, 18));
    }

    @Test
    public void getExpenseSumWithCategory() throws Exception {
        double sum = transactionDao.getExpenseSumWithCategory(9, 2021, 18, -4);
        assertEquals(100, sum);
    }

    @Test
    public void getExpenseSumExceptionException() {
        assertThrows(Exception.class, () -> transactionDao.getExpenseSumWithCategory(9, 2020, 18, -4));
    }

    @Test
    public void getBetweenDate() {
        List<Transaction> transactions = transactionDao.getBetweenDate("2021-10-01", "2021-10-05", 18);
        int[] ids = {36, 37, 38};
        assertFalse(transactions.isEmpty());
        assertEquals(3, transactions.size());
        for (int i = 0; i < transactions.size(); i++) {
            assertEquals(ids[i], transactions.get(i).getId());
        }
    }

    @Test
    public void getBetweenDateWrongToAndFrom() {
        List<Transaction> transactions = transactionDao.getBetweenDate("2021-10-05", "2021-10-01", 18);
        assertTrue(transactions.isEmpty());
    }

    @Test
    public void getBetweenDateOneTransaction() {
        List<Transaction> transactions = transactionDao.getBetweenDate("2021-10-01", "2021-10-01", 18);
        int[] ids = {36};
        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
        for (int i = 0; i < transactions.size(); i++) {
            assertEquals(ids[i], transactions.get(i).getId());
        }
    }

    @Test
    public void update() {
        Transaction transaction = transactionDao.find(32);
        assertNotNull(transaction);
        assertEquals(TypeTransaction.EXPENSE, transaction.getTypeTransaction());
        transaction.setTypeTransaction(TypeTransaction.INCOME);
        transactionDao.update(transaction);
        Transaction updatedTrans = transactionDao.find(32);
        assertEquals(TypeTransaction.INCOME, updatedTrans.getTypeTransaction());
    }
}
