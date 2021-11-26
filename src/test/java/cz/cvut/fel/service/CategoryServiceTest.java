package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.NotValidDataException;
import generator.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.SessionRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
public class CategoryServiceTest {
    private CategoryService categoryService;
    private CategoryDao categoryDao;
    private User user = Generator.generateDefaultUser();
    private UserDao userDao;
    private TransactionDao transactionDao;
    private BankAccountDao bankAccountDao;
    private BudgetDao budgetDao;
    private NotifyBudgetDao notifyBudgetDao;

    @MockBean
    private SessionRepository sessionRepository;

    @BeforeEach
    public void setUp() {
        categoryDao = mock(CategoryDao.class);
        transactionDao = mock(TransactionDao.class);
        bankAccountDao = mock(BankAccountDao.class);
        budgetDao = mock(BudgetDao.class);
        DebtDao debtDao = mock(DebtDao.class);
        notifyBudgetDao = mock(NotifyBudgetDao.class);
        NotifyDebtDao notifyDebtDao = mock(NotifyDebtDao.class);
        userDao = mock(UserDao.class);

        categoryService = new CategoryService(userDao, bankAccountDao, transactionDao,
                budgetDao, debtDao, categoryDao,
                notifyBudgetDao, notifyDebtDao);
    }

    @Test
    public void persist_mockTest_success() throws Exception {
        Category category = Generator.generateDefaultCategory();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(categoryDao.getUsersCategoryByName(anyInt(), anyString())).thenReturn(null);
            when(categoryDao.persist(category)).thenReturn(category);
            Category persisted = categoryService.persist(category);
            verify(categoryDao, times(1)).persist(category);
            assertEquals(category, persisted);
        }
    }

    @Test
    public void remove_MockTest_success() throws Exception {
        Category category = Generator.generateDefaultCategory();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(categoryDao.find(anyInt())).thenReturn(category);
            when(categoryDao.getUsersCategoryById(anyInt(), anyInt())).thenReturn(category);
            categoryService.removeCategory(category.getId());
            verify(categoryDao, times(1)).remove(category);
        }
    }

    @Test
    public void remove_MockTest_expectedThatTransactionsHaveNoCategory() throws Exception {
        Transaction transaction = Generator.generateDefaultTransaction();
        Category category = Generator.generateDefaultCategory();

        Category noCategory = Generator.generateDefaultCategory();
        noCategory.setName("No category");

        transaction.setCategory(category);
        category.getTransactions().add(transaction);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(categoryDao.find(category.getId())).thenReturn(category);
            when(categoryDao.find(-12)).thenReturn(noCategory);
            when(categoryDao.getUsersCategoryById(anyInt(), anyInt())).thenReturn(category);
            categoryService.removeCategory(category.getId());
            verify(categoryDao, times(1)).remove(category);
            assertEquals("No category", transaction.getCategory().getName());
        }
    }

    @Test
    public void remove_MockTest_expectedThatBudgetsAreDeleted() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setId(3);
        bankAccount.setCreator(user);

        Category noCategory = Generator.generateDefaultCategory();
        noCategory.setId(-12);
        noCategory.setName("No category");

        Category category = Generator.generateDefaultCategory();
        category.setId(1);

        Budget budget = Generator.generateDefaultBudget();
        budget.setId(2);
        budget.getCategory().add(category);
        category.getBudget().add(budget);
        budget.setBankAccount(bankAccount);

        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(categoryDao.find(category.getId())).thenReturn(category);
            when(categoryDao.find(-12)).thenReturn(noCategory);
            when(categoryDao.getUsersCategoryById(anyInt(), anyInt())).thenReturn(category);
            when(budgetDao.find(budget.getId())).thenReturn(budget);
            when(notifyBudgetDao.getNotifyBudgetByBudgetId(budget.getId())).thenReturn(Collections.emptyList());

            categoryService.removeCategory(category.getId());
            verify(categoryDao, times(1)).remove(category);
            verify(budgetDao, times(1)).remove(budget);
        }
    }

    @Test
    public void removeDefaultCategory_MockTest_throwException() {
        assertThrows(Exception.class, () -> {
            categoryService.removeCategory(-1);
        });
    }

    @Test
    public void updateName_MockTest_success() throws Exception {
        Category category = Generator.generateDefaultCategory();
        category.setId(1);
        category.setName("name");
        String name = "Test name";
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(categoryDao.find(anyInt())).thenReturn(category);
            when(categoryDao.getUsersCategoryById(anyInt(), anyInt())).thenReturn(category);
            when(categoryDao.getUsersCategoryByName(user.getId(), category.getName())).thenReturn(null);

            categoryService.updateCategoryName(category.getId(), name);
            verify(categoryDao, times(1)).update(category);
            assertEquals("Test name", category.getName());
        }
    }

    @Test
    public void updateName_MockTest_throwNotValidData() throws Exception {
        Category category = Generator.generateDefaultCategory();
        category.setId(-1);
        category.setName("name");
        String name = "Test name";
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(categoryDao.find(anyInt())).thenReturn(category);
            when(categoryDao.getUsersCategoryById(anyInt(), anyInt())).thenReturn(category);

            assertThrows(NotValidDataException.class, () -> categoryService.updateCategoryName(category.getId(), name));
        }
    }

    @Test
    public void find_MockTest_success() throws Exception {
        Category category = Generator.generateDefaultCategory();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(categoryDao.find(anyInt())).thenReturn(category);
            when(categoryDao.getUsersCategoryById(anyInt(), anyInt())).thenReturn(category);
            Category founded = categoryService.getByIdCategory(category.getId());
            verify(categoryDao, times(1)).find(anyInt());
            assertEquals(category, founded);
        }
    }

    @Test
    public void addTransactionToCategory_MockTest_success() throws Exception {
        Category category = Generator.generateDefaultCategory();
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        Transaction transaction = Generator.generateDefaultTransaction();
        transaction.setBankAccount(bankAccount);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(categoryDao.find(anyInt())).thenReturn(category);
            when(categoryDao.getUsersCategoryById(anyInt(), anyInt())).thenReturn(category);
            when(transactionDao.find(transaction.getId())).thenReturn(transaction);
            when(bankAccountDao.getUsersAvailableBankAccountById(user.getId(), bankAccount.getId())).thenReturn(bankAccount);

            categoryService.addTransactionToCategory(transaction.getId(), category.getId());
            verify(categoryDao, times(1)).update(any());
            verify(transactionDao, times(1)).update(any());
            assertEquals(1, category.getTransactions().size());
            assertEquals(transaction, category.getTransactions().get(0));
        }
    }
}
