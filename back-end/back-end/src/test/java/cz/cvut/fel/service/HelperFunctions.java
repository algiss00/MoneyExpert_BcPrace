package cz.cvut.fel.service;

import cz.cvut.fel.dao.BankAccountDao;
import cz.cvut.fel.dao.BudgetDao;
import cz.cvut.fel.dao.CategoryDao;
import cz.cvut.fel.dao.UserDao;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.Category;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import generator.Generator;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class HelperFunctions {

    public static void authUser(MockedStatic<SecurityUtils> utilities, UserDao userDao, User user) {
        utilities.when(SecurityUtils::getCurrentUser).thenReturn(user);
        assertEquals(user, SecurityUtils.getCurrentUser());
        when(userDao.find(anyInt())).thenReturn(user);
    }

    public static void prepareBudgetServiceTest(Budget budget, BankAccount bankAccount, Category category, CategoryDao categoryDao,
                                                BankAccountDao bankAccountDao, BudgetDao budgetDao) throws Exception {

        when(categoryDao.getUsersCategoryByName(anyInt(), anyString())).thenReturn(null);
        when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
        when(budgetDao.persist(budget)).thenReturn(budget);
        when(bankAccountDao.getUsersBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
        when(categoryDao.find(anyInt())).thenReturn(category);
        when(categoryDao.getUsersCategoryById(anyInt(), anyInt())).thenReturn(category);
    }

    public static void prepareTransactionServiceTest(BankAccount bankAccount, Category category, CategoryDao categoryDao,
                                                BankAccountDao bankAccountDao) throws Exception {

        when(categoryDao.getUsersCategoryByName(anyInt(), anyString())).thenReturn(null);
        when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
        when(bankAccountDao.getUsersBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
        when(categoryDao.find(anyInt())).thenReturn(category);
        when(categoryDao.getUsersCategoryById(anyInt(), anyInt())).thenReturn(category);
    }
}
