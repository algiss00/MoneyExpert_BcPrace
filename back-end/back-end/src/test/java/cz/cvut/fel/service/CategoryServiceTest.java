package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.Category;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
public class CategoryServiceTest {
    private CategoryService categoryService;
    private CategoryDao categoryDao;
    private User user = Generator.generateDefaultUser();
    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        categoryDao = mock(CategoryDao.class);
        TransactionDao transactionDao = mock(TransactionDao.class);
        BankAccountDao bankAccountDao = mock(BankAccountDao.class);
        BudgetDao budgetDao = mock(BudgetDao.class);
        DebtDao debtDao = mock(DebtDao.class);
        NotifyBudgetDao notifyBudgetDao = mock(NotifyBudgetDao.class);
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
            categoryService.remove(category.getId());
            verify(categoryDao, times(1)).remove(category);
        }
    }

    @Test
    public void update_MockTest_success() throws Exception {
        Category category = Generator.generateDefaultCategory();
        category.setName("mock test");
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(categoryDao.find(anyInt())).thenReturn(category);
            when(categoryDao.getUsersCategoryById(anyInt(), anyInt())).thenReturn(category);
            when(categoryDao.update(category)).thenReturn(category);

            Category updated = categoryService.updateCategory(category.getId(), category);
            verify(categoryDao, times(1)).update(category);
            assertEquals(category, updated);
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
}
