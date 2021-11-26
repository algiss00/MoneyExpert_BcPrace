package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.Category;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.NotValidDataException;
import generator.Generator;
import org.apache.catalina.startup.Catalina;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
public class BudgetServiceTest {
    private BudgetService budgetService;
    private BudgetDao budgetDao;
    private User user = Generator.generateDefaultUser();
    private UserDao userDao;
    private CategoryDao categoryDao;
    private BankAccountDao bankAccountDao;
    private NotifyBudgetDao notifyBudgetDao;

    @MockBean
    private SessionRepository sessionRepository;

    @BeforeEach
    public void setUp() {
        budgetDao = mock(BudgetDao.class);
        categoryDao = mock(CategoryDao.class);
        TransactionDao transactionDao = mock(TransactionDao.class);
        bankAccountDao = mock(BankAccountDao.class);
        DebtDao debtDao = mock(DebtDao.class);
        notifyBudgetDao = mock(NotifyBudgetDao.class);
        NotifyDebtDao notifyDebtDao = mock(NotifyDebtDao.class);
        userDao = mock(UserDao.class);

        budgetService = new BudgetService(userDao, bankAccountDao, transactionDao,
                budgetDao, debtDao, categoryDao,
                notifyBudgetDao, notifyDebtDao);
    }

    @Test
    public void persist_mockTest_success() throws Exception {
        Budget budget = Generator.generateDefaultBudget();
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        Category category = Generator.generateDefaultCategory();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            HelperFunctions.prepareBudgetServiceTest(budget, bankAccount, category, categoryDao, bankAccountDao, budgetDao);

            Budget persisted = budgetService.persist(budget, bankAccount.getId(), category.getId());
            verify(budgetDao, times(1)).persist(budget);
            assertEquals(budget, persisted);
        }
    }

    @Test
    public void remove_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        Budget budget = Generator.generateDefaultBudget();
        budget.setBankAccount(bankAccount);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(budgetDao.find(anyInt())).thenReturn(budget);
            when(notifyBudgetDao.getNotifyBudgetByBudgetId(anyInt())).thenReturn(Collections.emptyList());

            budgetService.removeBudget(budget.getId());
            verify(budgetDao, times(1)).remove(budget);
        }
    }

    @Test
    public void updateBudgetName_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        Budget budget = Generator.generateDefaultBudget();
        budget.setBankAccount(bankAccount);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(budgetDao.update(budget)).thenReturn(budget);
            when(budgetDao.find(anyInt())).thenReturn(budget);

            Budget updated = budgetService.updateBudgetName(budget.getId(), "TestName");
            verify(budgetDao, times(1)).update(budget);
            assertEquals("TestName", updated.getName());
        }
    }

    @Test
    public void updateBudgetAmount_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setId(1);
        bankAccount.setCreator(user);
        Budget budget = Generator.generateDefaultBudget();
        budget.setId(2);
        budget.setAmount(1000);
        budget.setBankAccount(bankAccount);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(budgetDao.update(budget)).thenReturn(budget);
            when(budgetDao.find(anyInt())).thenReturn(budget);

            budgetService.updateBudgetAmount(budget.getId(), 100D);
            verify(budgetDao, times(1)).update(budget);
            assertEquals(100, budget.getAmount());
        }
    }

    @Test
    public void updateBudgetPercent_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setId(1);
        bankAccount.setCreator(user);
        Budget budget = Generator.generateDefaultBudget();
        budget.setId(2);
        budget.setAmount(1000);
        budget.setPercentNotify(50);
        budget.setBankAccount(bankAccount);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(budgetDao.update(budget)).thenReturn(budget);
            when(budgetDao.find(anyInt())).thenReturn(budget);

            budgetService.updateBudgetPercent(budget.getId(), 75);
            verify(budgetDao, times(1)).update(budget);
            assertEquals(75, budget.getPercentNotify());
        }
    }

    @Test
    public void updateBudgetCategory_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setId(1);
        bankAccount.setCreator(user);

        Category category = Generator.generateDefaultCategory();
        category.setId(3);
        category.getCreators().add(user);
        category.setName("Test Category");

        Category category2 = Generator.generateDefaultCategory();
        category2.setId(-2);
        category2.getCreators().add(user);
        category2.setName("Test Category -2");

        Budget budget = Generator.generateDefaultBudget();
        budget.setId(2);
        budget.setAmount(1000);
        budget.setPercentNotify(50);
        budget.setBankAccount(bankAccount);
        budget.getCategory().add(category);
        bankAccount.getBudgets().add(budget);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(budgetDao.find(anyInt())).thenReturn(budget);
            when(categoryDao.find(-2)).thenReturn(category2);
            when(categoryDao.getUsersCategoryById(anyInt(), anyInt())).thenReturn(category2);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);

            budgetService.updateBudgetCategory(budget.getId(), -2);
            verify(budgetDao, times(1)).update(budget);
            assertEquals(-2, budget.getCategory().get(0).getId());
        }
    }

    @Test
    public void updateBudgetCategory_mockTest_throwNotValidDataException() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setId(1);
        bankAccount.setCreator(user);

        Category category2 = Generator.generateDefaultCategory();
        category2.setId(-2);
        category2.getCreators().add(user);
        category2.setName("Test Category -2");

        Budget budget2 = Generator.generateDefaultBudget();
        budget2.setId(4);
        budget2.getCategory().add(category2);
        bankAccount.getBudgets().add(budget2);

        Category category = Generator.generateDefaultCategory();
        category.setId(3);
        category.getCreators().add(user);
        category.setName("Test Category");

        Budget budget = Generator.generateDefaultBudget();
        budget.setId(2);
        budget.setAmount(1000);
        budget.setPercentNotify(50);
        budget.setBankAccount(bankAccount);
        budget.getCategory().add(category);
        bankAccount.getBudgets().add(budget);

        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(budgetDao.find(budget.getId())).thenReturn(budget);
            when(budgetDao.getByCategory(category2.getId(), bankAccount.getId())).thenReturn(budget2);
            when(categoryDao.find(-2)).thenReturn(category2);
            when(categoryDao.getUsersCategoryById(anyInt(), anyInt())).thenReturn(category2);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);

            assertThrows(NotValidDataException.class, () -> budgetService.updateBudgetCategory(budget.getId(), -2));
        }
    }

    @Test
    public void find_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        Budget budget = Generator.generateDefaultBudget();
        budget.setBankAccount(bankAccount);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(budgetDao.find(anyInt())).thenReturn(budget);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);

            Budget founded = budgetService.getByIdBudget(budget.getId());
            verify(budgetDao, times(1)).find(budget.getId());
            assertEquals(budget, founded);
        }
    }

    @Test
    public void find_mockTest_throwNotAuthenticatedClient() {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        BankAccount bankAccount2 = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);

        Budget budget = Generator.generateDefaultBudget();
        // belongs to other bankAccount
        budget.setBankAccount(bankAccount2);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(budgetDao.find(anyInt())).thenReturn(budget);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);

            assertThrows(NotAuthenticatedClient.class, () -> {
                budgetService.getByIdBudget(budget.getId());
            });
        }
    }
}
