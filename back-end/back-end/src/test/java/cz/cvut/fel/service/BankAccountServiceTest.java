package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeCurrency;
import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import generator.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

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
    private NotifyBudgetDao notifyBudgetDao;
    private NotifyDebtDao notifyDebtDao;

    @BeforeEach
    public void setUp() {
        bankAccountDao = mock(BankAccountDao.class);
        transactionDao = mock(TransactionDao.class);
        budgetDao = mock(BudgetDao.class);
        debtDao = mock(DebtDao.class);
        categoryDao = mock(CategoryDao.class);
        notifyBudgetDao = mock(NotifyBudgetDao.class);
        notifyDebtDao = mock(NotifyDebtDao.class);
        userDao = mock(UserDao.class);

        bankAccountService = new BankAccountService(userDao, bankAccountDao, transactionDao,
                budgetDao, debtDao, categoryDao,
                notifyBudgetDao, notifyDebtDao);
    }

    @Test
    public void persist_withoutStartTransaction_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
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
        bankAccount.setCreator(user);
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
        bankAccount.setCreator(user);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersAvailableBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
            bankAccountService.removeBankAcc(bankAccount.getId());
            verify(bankAccountDao, times(1)).remove(bankAccount);
        }
    }

    @Test
    public void removeByOwner_MockTest_throwNotAuthenticatedClient() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);

        User user2 = Generator.generateDefaultUser();
        bankAccount.getOwners().add(user2);
        user2.getAvailableBankAccounts().add(bankAccount);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(user2);
            assertEquals(user2, SecurityUtils.getCurrentUser());
            when(userDao.find(user2.getId())).thenReturn(user2);

            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersAvailableBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);

            assertThrows(NotAuthenticatedClient.class, () -> {
                bankAccountService.removeBankAcc(bankAccount.getId());
            });
        }
    }

    @Test
    public void update_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);

        BankAccount bankAccount2 = Generator.generateDefaultBankAccount();
        bankAccount2.setName("mock-test");
        bankAccount2.setCurrency(TypeCurrency.EUR);
        bankAccount2.setBalance(100D);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersAvailableBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.update(any())).thenReturn(bankAccount);

            bankAccountService.updateAccount(bankAccount.getId(), bankAccount2);

            assertEquals("mock-test", bankAccount.getName());
            assertEquals(TypeCurrency.EUR, bankAccount.getCurrency());
            assertEquals(100D, bankAccount.getBalance());

            verify(bankAccountDao, times(1)).update(bankAccount);
        }
    }

    @Test
    public void find_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);

            BankAccount returned = bankAccountService.getByIdBankAccount(bankAccount.getId());
            assertEquals(bankAccount, returned);
        }
    }

    @Test
    public void find_mockTest_throwNotAuthenticatedClient() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersAvailableBankAccountById(anyInt(), anyInt())).thenReturn(null);

            assertThrows(NotAuthenticatedClient.class, () -> {
                bankAccountService.getByIdBankAccount(bankAccount.getId());
            });
        }
    }

    @Test
    public void addNewOwner_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersAvailableBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);

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
        bankAccount.setCreator(user);

        User user2 = Generator.generateDefaultUser();
        bankAccount.getOwners().add(user2);
        user2.getAvailableBankAccounts().add(bankAccount);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(userDao.find(user2.getId())).thenReturn(user2);
            when(bankAccountDao.getUsersAvailableBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);

            bankAccountService.removeOwner(user2.getId(), bankAccount.getId());
            assertTrue(bankAccount.getOwners().isEmpty());
            assertTrue(user.getAvailableBankAccounts().isEmpty());

            verify(bankAccountDao, times(1)).update(bankAccount);
            verify(userDao, times(1)).update(user2);
        }
    }

    @Test
    public void removeCreator_mockTest_throwNotAuthenticatedClient() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersAvailableBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);

            assertThrows(NotAuthenticatedClient.class, () -> {
                bankAccountService.removeOwner(user.getId(), bankAccount.getId());
            });
        }
    }

    @Test
    public void removeOwnerFromAnotherBankAcc_mockTest_throwNotAuthenticatedClient() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        User user2 = Generator.generateDefaultUser();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersAvailableBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);

            assertThrows(NotAuthenticatedClient.class, () -> {
                bankAccountService.removeOwner(user2.getId(), bankAccount.getId());
            });
        }
    }

    @Test
    public void removeBudget_MockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        Budget budget = Generator.generateDefaultBudget();
        budget.setBankAccount(bankAccount);
        bankAccount.getBudgets().add(budget);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersAvailableBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
            when(budgetDao.find(anyInt())).thenReturn(budget);
            when(budgetDao.getByBankAcc(anyInt(), anyInt())).thenReturn(budget);
            when(notifyBudgetDao.getNotifyBudgetByBudgetId(anyInt())).thenReturn(Collections.emptyList());

            bankAccountService.removeBudgetFromBankAcc(budget.getId(), bankAccount.getId());
            verify(bankAccountDao, times(1)).update(bankAccount);
            assertTrue(bankAccount.getBudgets().isEmpty());
        }
    }

    @Test
    public void removeDebt_MockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        Debt debt = Generator.generateDefaultDebt();
        debt.setBankAccount(bankAccount);
        bankAccount.getDebts().add(debt);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersAvailableBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
            when(debtDao.find(anyInt())).thenReturn(debt);
            when(debtDao.getByBankAccId(anyInt(), anyInt())).thenReturn(debt);
            when(notifyDebtDao.getNotifyDebtByDebtId(anyInt())).thenReturn(Collections.emptyList());

            bankAccountService.removeDebt(debt.getId(), bankAccount.getId());
            verify(bankAccountDao, times(1)).update(bankAccount);
            assertTrue(bankAccount.getDebts().isEmpty());
        }
    }

    @Test
    public void removeTransaction_MockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        Transaction transaction = Generator.generateDefaultTransaction();
        transaction.setBankAccount(bankAccount);
        bankAccount.getTransactions().add(transaction);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersAvailableBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
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
