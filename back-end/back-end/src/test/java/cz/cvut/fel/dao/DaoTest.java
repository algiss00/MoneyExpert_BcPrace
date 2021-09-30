package cz.cvut.fel.dao;

import cz.cvut.fel.MoneyExpertApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
public class DaoTest {
    @Autowired
    protected BankAccountDao bankAccountDao;
    @Autowired
    protected BudgetDao budgetDao;
    @Autowired
    protected CategoryDao categoryDao;
    @Autowired
    protected DebtDao debtDao;
    @Autowired
    protected NotifyBudgetDao notifyBudgetDao;
    @Autowired
    protected NotifyDebtDao notifyDebtDao;
    @Autowired
    protected TransactionDao transactionDao;
    @Autowired
    protected UserDao userDao;

    @Test
    public void testRepositories() {
        assertNotNull(bankAccountDao);
        assertNotNull(budgetDao);
        assertNotNull(categoryDao);
        assertNotNull(debtDao);
        assertNotNull(notifyBudgetDao);
        assertNotNull(notifyDebtDao);
        assertNotNull(transactionDao);
        assertNotNull(userDao);
    }
}
