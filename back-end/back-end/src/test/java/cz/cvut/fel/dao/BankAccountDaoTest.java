package cz.cvut.fel.dao;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.model.BankAccount;
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
public class BankAccountDaoTest {
    @Autowired
    private BankAccountDao bankAccountDao;

    @Test
    public void findBankAccount() {
        BankAccount bankAccount = bankAccountDao.find(15);
        assertNotNull(bankAccount);
        assertEquals("CSOB Ucet", bankAccount.getName());
        assertEquals("CZK", bankAccount.getCurrency());
        assertEquals(1000, bankAccount.getBalance(), 0.0);
    }

    @Test
    public void getByName() {
        List<BankAccount> bankAccounts = bankAccountDao.getByName("CSOB Ucet", 10);
        assertFalse(bankAccounts.isEmpty());
        assertEquals(2, bankAccounts.size());

        int[] ids = {15, 19};
        for (int i = 0; i < bankAccounts.size(); i++) {
            assertEquals(ids[i], bankAccounts.get(i).getId());
            assertEquals("CSOB Ucet", bankAccounts.get(i).getName());
        }
    }

    @Test
    public void getByNameEmpty() {
        List<BankAccount> bankAccounts = bankAccountDao.getByName("CSOB Ucet Test", 10);
        assertTrue(bankAccounts.isEmpty());
    }

    @Test
    public void getUsersBankAccountById() throws Exception {
        BankAccount bankAccount = bankAccountDao.getUsersBankAccountById(10, 15);
        assertNotNull(bankAccount);
        assertEquals("CSOB Ucet", bankAccount.getName());
        assertEquals("CZK", bankAccount.getCurrency());
        assertEquals(1000, bankAccount.getBalance(), 0.0);
    }

    @Test
    public void getUsersBankAccountByIdNotFound() throws Exception {
        BankAccount bankAccount = bankAccountDao.getUsersBankAccountById(10, 17);
        assertNull(bankAccount);
    }

    @Test
    public void persist() {
        BankAccount generatedBankAcc = Generator.generateDefaultBankAccount();
        BankAccount persistedBankAcc = bankAccountDao.persist(generatedBankAcc);
        assertNotNull(persistedBankAcc);
        assertEquals(generatedBankAcc.getName(), persistedBankAcc.getName());
        assertEquals(generatedBankAcc.getCurrency(), persistedBankAcc.getCurrency());
        assertEquals(generatedBankAcc.getBalance(), persistedBankAcc.getBalance(), 0.0);
    }

    @Test
    public void remove() {
        BankAccount bankAccount = bankAccountDao.find(17);
        assertNotNull(bankAccount);
        bankAccountDao.remove(bankAccount);
        assertNull(bankAccountDao.find(17));
    }

    @Test
    public void update() {
        BankAccount bankAccount = bankAccountDao.find(17);
        assertNotNull(bankAccount);
        assertEquals("Pracovni Ucet", bankAccount.getName());
        bankAccount.setName("Test update ucet");
        bankAccountDao.update(bankAccount);
        BankAccount updatedBankAccount = bankAccountDao.find(17);
        assertEquals("Test update ucet", updatedBankAccount.getName());
    }
}
