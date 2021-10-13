package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import generator.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
public class TransactionServiceTest {
    private TransactionService transactionService;
    private TransactionDao transactionDao;
    private User user = Generator.generateDefaultUser();
    private UserDao userDao;
    private CategoryDao categoryDao;
    private BankAccountDao bankAccountDao;

    @BeforeEach
    public void setUp() {
        transactionDao = mock(TransactionDao.class);
        NotifyDebtDao notifyDebtDao = mock(NotifyDebtDao.class);
        bankAccountDao = mock(BankAccountDao.class);
        BudgetDao budgetDao = mock(BudgetDao.class);
        categoryDao = mock(CategoryDao.class);
        DebtDao debtDao = mock(DebtDao.class);
        NotifyBudgetDao notifyBudgetDao = mock(NotifyBudgetDao.class);
        userDao = mock(UserDao.class);

        transactionService = new TransactionService(userDao, bankAccountDao, transactionDao,
                budgetDao, debtDao, categoryDao, notifyBudgetDao, notifyDebtDao);
    }

    @Test
    public void persist_mockTest_success() throws Exception {
        Transaction transaction = Generator.generateDefaultTransaction();
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        Category category = Generator.generateDefaultCategory();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            HelperFunctions.prepareTransactionServiceTest(bankAccount, category, categoryDao, bankAccountDao);
            when(transactionDao.persist(transaction)).thenReturn(transaction);

            Transaction persisted = transactionService.persist(transaction, bankAccount.getId(), category.getId());
            verify(transactionDao, times(1)).persist(transaction);
            assertEquals(transaction, persisted);
        }
    }

    @Test
    public void remove_mockTest_success() throws Exception {
        Transaction transaction = Generator.generateDefaultTransaction();
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        transaction.setBankAccount(bankAccount);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(transactionDao.find(transaction.getId())).thenReturn(transaction);
            when(bankAccountDao.getUsersBankAccountById(user.getId(), bankAccount.getId())).thenReturn(bankAccount);

            transactionService.remove(transaction.getId());
            verify(transactionDao, times(1)).remove(transaction);
        }
    }

    @Test
    public void update_mockTest_success() throws Exception {
        Transaction transaction = Generator.generateDefaultTransaction();
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        transaction.setBankAccount(bankAccount);
        transaction.setJottings("mock test");
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(transactionDao.update(transaction)).thenReturn(transaction);
            when(transactionDao.find(transaction.getId())).thenReturn(transaction);
            when(bankAccountDao.getUsersBankAccountById(user.getId(), bankAccount.getId())).thenReturn(bankAccount);

            Transaction updated = transactionService.update(transaction.getId(), transaction);
            verify(transactionDao, times(1)).update(transaction);
            assertEquals(transaction, updated);
        }
    }

    @Test
    public void find_mockTest_success() throws Exception {
        Transaction transaction = Generator.generateDefaultTransaction();
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        transaction.setBankAccount(bankAccount);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(transactionDao.find(transaction.getId())).thenReturn(transaction);
            when(bankAccountDao.getUsersBankAccountById(user.getId(), bankAccount.getId())).thenReturn(bankAccount);

            Transaction founded = transactionService.getByIdTransaction(transaction.getId());
            verify(transactionDao, times(1)).find(transaction.getId());
            assertEquals(transaction, founded);
        }
    }
}
