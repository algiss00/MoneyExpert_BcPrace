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
    private CategoryDao categoryDao;
    private TransactionDao transactionDao;
    private BudgetDao budgetDao;
    private DebtDao debtDao;

    @BeforeEach
    public void setUp() {
        bankAccountDao = mock(BankAccountDao.class);
        transactionDao = mock(TransactionDao.class);
        budgetDao = mock(BudgetDao.class);
        debtDao = mock(DebtDao.class);
        categoryDao = mock(CategoryDao.class);
        NotifyBudgetDao notifyBudgetDao = mock(NotifyBudgetDao.class);
        NotifyDebtDao notifyDebtDao = mock(NotifyDebtDao.class);
        userDao = mock(UserDao.class);

        bankAccountService = new BankAccountService(userDao, bankAccountDao, transactionDao,
                budgetDao, debtDao, categoryDao,
                notifyBudgetDao, notifyDebtDao);
    }

    @Test
    public void persist_withoutStartTransaction_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setBalance(0D);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.persist(bankAccount)).thenReturn(bankAccount);
            BankAccount persisted = bankAccountService.persist(bankAccount);
            verify(bankAccountDao, times(1)).persist(bankAccount);
            assertEquals(bankAccount, persisted);
        }
    }

    @Test
    public void persist_withStartTransaction_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        Category category = Generator.generateDefaultCategory();
        Transaction transaction = Generator.generateDefaultTransaction();
        transaction.setBankAccount(bankAccount);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.persist(bankAccount)).thenReturn(bankAccount);
            when(categoryDao.find(anyInt())).thenReturn(category);
            when(categoryDao.getUsersCategoryById(anyInt(), anyInt())).thenReturn(category);
            when(transactionDao.persist(any())).thenReturn(transaction);

            BankAccount persisted = bankAccountService.persist(bankAccount);
            verify(bankAccountDao, times(1)).persist(bankAccount);
            verify(transactionDao, times(1)).persist(any());
            verify(bankAccountDao, times(1)).update(persisted);
            assertFalse(persisted.getTransactions().isEmpty());
            assertEquals(transaction, persisted.getTransactions().get(0));
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

    @Test
    public void addNewOwner_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);

            bankAccountService.addNewOwner(user.getId(), bankAccount.getId());
            assertEquals(1, bankAccount.getOwners().size());
            assertEquals(bankAccount.getOwners().get(0), user);
            assertEquals(1, user.getAvailableBankAccounts().size());
            assertEquals(user.getAvailableBankAccounts().get(0), bankAccount);

            verify(bankAccountDao, times(1)).update(bankAccount);
            verify(userDao, times(1)).update(user);
        }
    }

    @Test
    public void removeOwner_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);

            bankAccountService.addNewOwner(user.getId(), bankAccount.getId());
            assertEquals(1, bankAccount.getOwners().size());
            assertEquals(bankAccount.getOwners().get(0), user);
            assertEquals(1, user.getAvailableBankAccounts().size());
            assertEquals(user.getAvailableBankAccounts().get(0), bankAccount);

            bankAccountService.removeOwner(user.getId(), bankAccount.getId());
            assertTrue(bankAccount.getOwners().isEmpty());
            assertTrue(user.getAvailableBankAccounts().isEmpty());

            verify(bankAccountDao, times(2)).update(bankAccount);
            verify(userDao, times(2)).update(user);
        }
    }

    @Test
    public void removeBudget_MockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        Budget budget = Generator.generateDefaultBudget();
        budget.setCreator(user);
        bankAccount.getBudgets().add(budget);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
            when(budgetDao.find(anyInt())).thenReturn(budget);
            when(budgetDao.getByBankAcc(anyInt(), anyInt())).thenReturn(budget);

            bankAccountService.removeBudgetFromBankAcc(budget.getId(), bankAccount.getId());
            verify(bankAccountDao, times(1)).update(bankAccount);
            verify(budgetDao, times(1)).update(budget);
            assertTrue(bankAccount.getBudgets().isEmpty());
        }
    }

    @Test
    public void removeDebt_MockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        Debt debt = Generator.generateDefaultDebt();
        debt.setCreator(user);
        bankAccount.getDebts().add(debt);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
            when(debtDao.find(anyInt())).thenReturn(debt);
            when(debtDao.getByBankAccId(anyInt(), anyInt())).thenReturn(debt);

            bankAccountService.removeDebt(debt.getId(), bankAccount.getId());
            verify(bankAccountDao, times(1)).update(bankAccount);
            verify(debtDao, times(1)).update(debt);
            assertTrue(bankAccount.getDebts().isEmpty());
        }
    }

    @Test
    public void removeTransaction_MockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        Transaction transaction = Generator.generateDefaultTransaction();
        transaction.setBankAccount(bankAccount);
        bankAccount.getTransactions().add(transaction);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
            when(transactionDao.find(anyInt())).thenReturn(transaction);
            when(transactionDao.getFromBankAcc(anyInt(), anyInt())).thenReturn(transaction);

            bankAccountService.removeTransFromAccount(transaction.getId(), bankAccount.getId());
            verify(bankAccountDao, times(1)).update(bankAccount);
            verify(transactionDao, times(1)).update(transaction);
            verify(transactionDao, times(1)).remove(transaction);
            assertTrue(bankAccount.getTransactions().isEmpty());
        }
    }
}
