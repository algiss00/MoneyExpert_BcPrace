package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import generator.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
public class BankAccountServiceTest {
    private BankAccountService bankAccountService;
    private BankAccountDao bankAccountDao;
    private User user = Generator.generateDefaultUser();
    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        bankAccountDao = mock(BankAccountDao.class);
        TransactionDao transactionDao = mock(TransactionDao.class);
        BudgetDao budgetDao = mock(BudgetDao.class);
        DebtDao debtDao = mock(DebtDao.class);
        CategoryDao categoryDao = mock(CategoryDao.class);
        NotifyBudgetDao notifyBudgetDao = mock(NotifyBudgetDao.class);
        NotifyDebtDao notifyDebtDao = mock(NotifyDebtDao.class);
        userDao = mock(UserDao.class);

        bankAccountService = new BankAccountService(userDao, bankAccountDao, transactionDao,
                budgetDao, debtDao, categoryDao,
                notifyBudgetDao, notifyDebtDao);
    }

    @Test
    public void persist_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setBalance(0);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.persist(bankAccount)).thenReturn(bankAccount);
            BankAccount persisted = bankAccountService.persist(bankAccount);
            verify(bankAccountDao, times(1)).persist(bankAccount);
            assertEquals(bankAccount, persisted);
        }
    }

    @Test
    public void remove_MockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
            bankAccountService.remove(bankAccount.getId());
            verify(bankAccountDao, times(1)).remove(bankAccount);
        }
    }

    @Test
    public void update_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setName("mock-test");
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.update(any())).thenReturn(bankAccount);
            assertEquals(bankAccount, bankAccountService.updateAccount(bankAccount.getId(), bankAccount));
            verify(bankAccountDao, times(1)).update(bankAccount);
        }
    }

    @Test
    public void find_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
            BankAccount returned = bankAccountService.getByIdBankAccount(bankAccount.getId());
            assertEquals(bankAccount, returned);
        }
    }
}
