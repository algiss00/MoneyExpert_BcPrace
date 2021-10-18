package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeNotification;
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
    private BudgetDao budgetDao;
    private NotifyBudgetDao notifyBudgetDao;

    @BeforeEach
    public void setUp() {
        transactionDao = mock(TransactionDao.class);
        NotifyDebtDao notifyDebtDao = mock(NotifyDebtDao.class);
        bankAccountDao = mock(BankAccountDao.class);
        budgetDao = mock(BudgetDao.class);
        categoryDao = mock(CategoryDao.class);
        DebtDao debtDao = mock(DebtDao.class);
        notifyBudgetDao = mock(NotifyBudgetDao.class);
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

    @Test
    public void persistBankAccountLogic_mockTest_success() throws Exception {
        Transaction transaction = Generator.generateDefaultTransaction();
        transaction.setAmount(100);
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        Category category = Generator.generateDefaultCategory();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            HelperFunctions.prepareTransactionServiceTest(bankAccount, category, categoryDao, bankAccountDao);
            when(transactionDao.persist(transaction)).thenReturn(transaction);

            Transaction persisted = transactionService.persist(transaction, bankAccount.getId(), category.getId());
            verify(transactionDao, times(1)).persist(transaction);
            assertEquals(transaction, persisted);
            assertEquals(900, bankAccount.getBalance());
        }
    }

    @Test
    public void persistBankAccountLogicWithBudgetLogic_mockTest_success() throws Exception {
        Category category = Generator.generateDefaultCategory();
        category.setName("mock test category");

        Budget budget = Generator.generateDefaultBudget();
        budget.setCategory(category);
        budget.setAmount(500);

        Transaction transaction = Generator.generateDefaultTransaction();
        transaction.setAmount(600);
        transaction.setCategory(category);

        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.getBudgets().add(budget);

        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            HelperFunctions.prepareTransactionServiceTest(bankAccount, category, categoryDao, bankAccountDao);
            when(transactionDao.persist(transaction)).thenReturn(transaction);
            when(budgetDao.getByCategory(transaction.getId(), bankAccount.getId())).thenReturn(budget);
            when(notifyBudgetDao.alreadyExistsBudget(budget.getId(), TypeNotification.BUDGET_AMOUNT)).thenReturn(false);

            Transaction persisted = transactionService.persist(transaction, bankAccount.getId(), category.getId());
            verify(transactionDao, times(1)).persist(transaction);
            verify(budgetDao, times(1)).update(budget);
            verify(notifyBudgetDao, times(1)).persist(any());

            assertEquals(transaction, persisted);
            assertEquals(400, bankAccount.getBalance());
            assertEquals(600, budget.getSumAmount());
        }
    }

    @Test
    public void transferTransaction_mockTest_success() throws Exception {
        Transaction transaction = Generator.generateDefaultTransaction();
        Category category = Generator.generateDefaultCategory();
        BankAccount fromBankAcc = Generator.generateDefaultBankAccount();
        fromBankAcc.setId(1);
        BankAccount toBankAcc = Generator.generateDefaultBankAccount();
        toBankAcc.setId(2);
        toBankAcc.setBalance(1500);

        transaction.setBankAccount(fromBankAcc);
        transaction.setCategory(category);

        fromBankAcc.getTransactions().add(transaction);
        // predpokladame ze startovni balance byl 2000 potom expense transaction 1000 a mame actual balance 1000
        fromBankAcc.setBalance(1000);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            HelperFunctions.prepareTransactionServiceTest(fromBankAcc, category, categoryDao, bankAccountDao);
            when(transactionDao.find(transaction.getId())).thenReturn(transaction);
            when(transactionDao.getFromBankAcc(fromBankAcc.getId(), transaction.getId())).thenReturn(transaction);
            when(bankAccountDao.find(toBankAcc.getId())).thenReturn(toBankAcc);
            when(bankAccountDao.getUsersBankAccountById(user.getId(), toBankAcc.getId())).thenReturn(toBankAcc);
            when(budgetDao.getByCategory(anyInt(), anyInt())).thenReturn(null);

            transactionService.transferTransaction(fromBankAcc.getId(), toBankAcc.getId(), transaction.getId());

            verify(transactionDao, times(1)).persist(any());
            verify(bankAccountDao, times(1)).update(toBankAcc);
            verify(bankAccountDao, times(1)).update(fromBankAcc);
            assertEquals(1, toBankAcc.getTransactions().size());
            assertEquals(500, toBankAcc.getBalance());
            assertEquals(0, fromBankAcc.getTransactions().size());
            assertEquals(2000, fromBankAcc.getBalance());
        }
    }
}
