package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeCurrency;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.dto.TypeTransaction;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
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
        transaction.setCategory(Generator.generateDefaultCategory());
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(transactionDao.find(transaction.getId())).thenReturn(transaction);
            when(bankAccountDao.getUsersAvailableBankAccountById(user.getId(), bankAccount.getId())).thenReturn(bankAccount);
            when(budgetDao.getByCategory(anyInt(), anyInt())).thenReturn(Generator.generateDefaultBudget());
            transactionService.remove(transaction.getId());
            verify(transactionDao, times(1)).remove(transaction);
        }
    }

    @Test
    public void update_mockTest_success() throws Exception {
        Transaction transaction = Generator.generateDefaultTransaction();
        Transaction updatedTransaction = Generator.generateDefaultTransaction();
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        transaction.setBankAccount(bankAccount);

        transaction.setCategory(Generator.generateDefaultCategory());
        updatedTransaction.setJottings("mock test");
        updatedTransaction.setAmount(100);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(transactionDao.find(transaction.getId())).thenReturn(transaction);
            when(bankAccountDao.getUsersAvailableBankAccountById(user.getId(), bankAccount.getId())).thenReturn(bankAccount);

            transactionService.updateBasic(transaction.getId(), updatedTransaction);
            verify(transactionDao, times(1)).update(transaction);
            assertEquals("mock test", transaction.getJottings());
            assertEquals(100, transaction.getAmount());
            assertEquals(1900, bankAccount.getBalance());
        }
    }

    @Test
    public void updateCategory_mockTest_success() throws Exception {
        Category category1 = Generator.generateDefaultCategory();
        Category category2 = Generator.generateDefaultCategory();
        Transaction transaction = Generator.generateDefaultTransaction();
        Transaction updatedTransaction = Generator.generateDefaultTransaction();
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        transaction.setBankAccount(bankAccount);
        transaction.setCategory(category1);

        updatedTransaction.setCategory(category2);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(transactionDao.update(transaction)).thenReturn(updatedTransaction);
            when(transactionDao.find(transaction.getId())).thenReturn(transaction);
            when(bankAccountDao.getUsersAvailableBankAccountById(user.getId(), bankAccount.getId())).thenReturn(bankAccount);
            when(categoryDao.find(category2.getId())).thenReturn(category2);
            when(categoryDao.getUsersCategoryById(user.getId(), category2.getId())).thenReturn(category2);

            Transaction updated = transactionService.updateCategoryTransaction(transaction.getId(), category2.getId());
            verify(transactionDao, times(1)).update(transaction);
            verify(categoryDao, times(1)).update(category2);
            assertEquals(updatedTransaction, updated);
        }
    }

//    @Test
//    public void updateCategoryCorrectBudgetLogic_mockTest_success() throws Exception {
//        Category category1 = Generator.generateDefaultCategory();
//        Category category2 = Generator.generateDefaultCategory();
//        Transaction transaction = Generator.generateDefaultTransaction();
//        Transaction updatedTransaction = Generator.generateDefaultTransaction();
//        BankAccount bankAccount = Generator.generateDefaultBankAccount();
//        transaction.setBankAccount(bankAccount);
//        transaction.setCategory(category1);
//
//        updatedTransaction.setCategory(category2);
//        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
//            HelperFunctions.authUser(utilities, userDao, user);
//            when(transactionDao.update(transaction)).thenReturn(updatedTransaction);
//            when(transactionDao.find(transaction.getId())).thenReturn(transaction);
//            when(bankAccountDao.getUsersAvailableBankAccountById(user.getId(), bankAccount.getId())).thenReturn(bankAccount);
//            when(categoryDao.find(category2.getId())).thenReturn(category2);
//            when(categoryDao.getUsersCategoryById(user.getId(), category2.getId())).thenReturn(category2);
//
//            Transaction updated = transactionService.updateCategoryTransaction(transaction.getId(), category2.getId());
//            verify(transactionDao, times(1)).update(transaction);
//            verify(categoryDao, times(1)).update(category2);
//            assertEquals(updatedTransaction, updated);
//        }
//    }

    @Test
    public void updateTransactionType_mockTest_success() throws Exception {
        Transaction transaction = Generator.generateDefaultTransaction();
        Transaction updatedTransaction = Generator.generateDefaultTransaction();
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        transaction.setBankAccount(bankAccount);

        transaction.setCategory(Generator.generateDefaultCategory());
        updatedTransaction.setTypeTransaction(TypeTransaction.INCOME);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(transactionDao.update(transaction)).thenReturn(updatedTransaction);
            when(transactionDao.find(transaction.getId())).thenReturn(transaction);
            when(bankAccountDao.getUsersAvailableBankAccountById(user.getId(), bankAccount.getId())).thenReturn(bankAccount);

            Transaction updated = transactionService.updateTransactionType(transaction.getId(), TypeTransaction.INCOME);
            verify(transactionDao, times(1)).update(transaction);
            verify(bankAccountDao, times(1)).update(bankAccount);
            assertEquals(updatedTransaction, updated);
            // predpokladame ze start balance bankAcc byl 2000 potom pribyl transaction expense 1000, ale ted mame update na income
            assertEquals(2000, bankAccount.getBalance());
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
            when(bankAccountDao.getUsersAvailableBankAccountById(user.getId(), bankAccount.getId())).thenReturn(bankAccount);

            Transaction founded = transactionService.getByIdTransaction(transaction.getId());
            verify(transactionDao, times(1)).find(transaction.getId());
            assertEquals(transaction, founded);
        }
    }

    /**
     * user nepatri k BankAccountu ve kterem je Transakce - user neni creator ani owner
     *
     * @throws Exception
     */
    @Test
    public void find_mockTest_throwNotAuthenticatedClient() throws Exception {
        Transaction transaction = Generator.generateDefaultTransaction();
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        transaction.setBankAccount(bankAccount);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(transactionDao.find(transaction.getId())).thenReturn(transaction);
            when(bankAccountDao.getUsersAvailableBankAccountById(user.getId(), bankAccount.getId())).thenReturn(null);

            assertThrows(NotAuthenticatedClient.class, () -> {
                transactionService.getByIdTransaction(transaction.getId());
            });
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

    /**
     * Pridani transakce a kontrola pokud budget a bankAccount logic funguji spravne
     * Tranascke ma amount 600 ale budget ma amount 500, tzn. ze pro budget se vytvori dve NotifyBudget entity pro Amount a PercentNotify
     * Pak kontrouju pokud BankAcc balance je spravny
     *
     * @throws Exception
     */
    @Test
    public void persistCheckBankAccountLogicWithBudgetLogic_mockTest_success() throws Exception {
        Category category = Generator.generateDefaultCategory();
        category.setName("mock test category");

        Budget budget = Generator.generateDefaultBudget();
        budget.getCategory().add(category);
        budget.setAmount(500);

        Transaction transaction = Generator.generateDefaultTransaction();
        transaction.setAmount(600);
        transaction.setCategory(category);

        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.getBudgets().add(budget);
        budget.setBankAccount(bankAccount);

        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            HelperFunctions.prepareTransactionServiceTest(bankAccount, category, categoryDao, bankAccountDao);
            when(transactionDao.persist(transaction)).thenReturn(transaction);
            when(budgetDao.getByCategory(transaction.getId(), bankAccount.getId())).thenReturn(budget);
            when(notifyBudgetDao.alreadyExistsBudget(budget.getId(), TypeNotification.BUDGET_AMOUNT)).thenReturn(false);

            Transaction persisted = transactionService.persist(transaction, bankAccount.getId(), category.getId());
            verify(transactionDao, times(1)).persist(transaction);
            verify(budgetDao, times(1)).update(budget);
            verify(notifyBudgetDao, times(2)).persist(any());

            assertEquals(transaction, persisted);
            assertEquals(400, bankAccount.getBalance());
            assertEquals(600, budget.getSumAmount());
        }
    }

    @Test
    public void transferTransactionCurrencyConvert_mockTest_success() throws Exception {
        Transaction transaction = Generator.generateDefaultTransaction();
        Category category = Generator.generateDefaultCategory();
        BankAccount fromBankAcc = Generator.generateDefaultBankAccount();
        fromBankAcc.setId(1);
        BankAccount toBankAcc = Generator.generateDefaultBankAccount();
        toBankAcc.setId(2);
        toBankAcc.setBalance(150D);
        toBankAcc.setCurrency(TypeCurrency.EUR);

        transaction.setBankAccount(fromBankAcc);
        transaction.setCategory(category);

        fromBankAcc.getTransactions().add(transaction);
        // predpokladame ze startovni balance byl 2000 potom expense transaction 1000 a mame actual balance 1000
        fromBankAcc.setBalance(1000D);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            HelperFunctions.prepareTransactionServiceTest(fromBankAcc, category, categoryDao, bankAccountDao);
            when(transactionDao.find(transaction.getId())).thenReturn(transaction);
            when(transactionDao.getFromBankAcc(fromBankAcc.getId(), transaction.getId())).thenReturn(transaction);
            when(bankAccountDao.find(toBankAcc.getId())).thenReturn(toBankAcc);
            when(bankAccountDao.getUsersAvailableBankAccountById(user.getId(), toBankAcc.getId())).thenReturn(toBankAcc);
            when(budgetDao.getByCategory(anyInt(), anyInt())).thenReturn(null);

            transactionService.transferTransaction(fromBankAcc.getId(), toBankAcc.getId(), transaction.getId());

            verify(transactionDao, times(1)).persist(any());
            verify(bankAccountDao, times(1)).update(toBankAcc);
            verify(bankAccountDao, times(1)).update(fromBankAcc);
            assertEquals(1, toBankAcc.getTransactions().size());
            assertEquals(111.0, toBankAcc.getBalance());
            assertEquals(0, fromBankAcc.getTransactions().size());
            assertEquals(2000, fromBankAcc.getBalance());
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
        toBankAcc.setBalance(1500D);

        transaction.setBankAccount(fromBankAcc);
        transaction.setCategory(category);

        fromBankAcc.getTransactions().add(transaction);
        // predpokladame ze startovni balance byl 2000 potom expense transaction 1000 a mame actual balance 1000
        fromBankAcc.setBalance(1000D);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            HelperFunctions.prepareTransactionServiceTest(fromBankAcc, category, categoryDao, bankAccountDao);
            when(transactionDao.find(transaction.getId())).thenReturn(transaction);
            when(transactionDao.getFromBankAcc(fromBankAcc.getId(), transaction.getId())).thenReturn(transaction);
            when(bankAccountDao.find(toBankAcc.getId())).thenReturn(toBankAcc);
            when(bankAccountDao.getUsersAvailableBankAccountById(user.getId(), toBankAcc.getId())).thenReturn(toBankAcc);
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
