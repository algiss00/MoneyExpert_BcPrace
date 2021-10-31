package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.Category;
import cz.cvut.fel.model.User;
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

    @BeforeEach
    public void setUp() {
        budgetDao = mock(BudgetDao.class);
        categoryDao = mock(CategoryDao.class);
        TransactionDao transactionDao = mock(TransactionDao.class);
        bankAccountDao = mock(BankAccountDao.class);
        DebtDao debtDao = mock(DebtDao.class);
        NotifyBudgetDao notifyBudgetDao = mock(NotifyBudgetDao.class);
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

            budgetService.removeBudget(budget.getId());
            verify(budgetDao, times(1)).remove(budget);
        }
    }

    @Test
    public void update_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        Budget budget = Generator.generateDefaultBudget();
        budget.setBankAccount(bankAccount);
        budget.setName("mock test");
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(budgetDao.update(budget)).thenReturn(budget);
            when(budgetDao.find(anyInt())).thenReturn(budget);

            Budget updated = budgetService.updateBudget(budget.getId(), budget);
            verify(budgetDao, times(1)).update(budget);
            assertEquals(budget, updated);
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
    public void find_mockTest_throwNotAuthenticatedClient() throws Exception {
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
