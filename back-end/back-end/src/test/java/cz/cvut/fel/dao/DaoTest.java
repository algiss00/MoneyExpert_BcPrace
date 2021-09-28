package cz.cvut.fel.dao;

import cz.cvut.fel.MoneyExpertApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
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
        Assert.assertNotNull(bankAccountDao);
        Assert.assertNotNull(budgetDao);
        Assert.assertNotNull(categoryDao);
        Assert.assertNotNull(debtDao);
        Assert.assertNotNull(notifyBudgetDao);
        Assert.assertNotNull(notifyDebtDao);
        Assert.assertNotNull(transactionDao);
        Assert.assertNotNull(userDao);
    }
}
