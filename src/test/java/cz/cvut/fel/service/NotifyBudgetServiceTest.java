package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.model.NotifyBudget;
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
public class NotifyBudgetServiceTest {
    private NotifyBudgetService notifyBudgetService;
    private NotifyBudgetDao notifyBudgetDao;
    private User user = Generator.generateDefaultUser();
    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        notifyBudgetDao = mock(NotifyBudgetDao.class);
        TransactionDao transactionDao = mock(TransactionDao.class);
        BankAccountDao bankAccountDao = mock(BankAccountDao.class);
        BudgetDao budgetDao = mock(BudgetDao.class);
        CategoryDao categoryDao = mock(CategoryDao.class);
        DebtDao debtDao = mock(DebtDao.class);
        NotifyDebtDao notifyDebtDao = mock(NotifyDebtDao.class);
        userDao = mock(UserDao.class);

        notifyBudgetService = new NotifyBudgetService(userDao, bankAccountDao, transactionDao,
                budgetDao, debtDao, categoryDao,
                notifyBudgetDao, notifyDebtDao);
    }

    @Test
    public void persist_mockTest_success() throws Exception {
        NotifyBudget notifyBudget = Generator.generateDefaultBudgetNotify();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(notifyBudgetDao.persist(notifyBudget)).thenReturn(notifyBudget);

            NotifyBudget persisted = notifyBudgetService.persistNotifyBudget(notifyBudget);
            verify(notifyBudgetDao, times(1)).persist(notifyBudget);
            assertEquals(notifyBudget, persisted);
        }
    }

    @Test
    public void remove_mockTest_success() throws Exception {
        NotifyBudget notifyBudget = Generator.generateDefaultBudgetNotify();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(notifyBudgetDao.find(notifyBudget.getId())).thenReturn(notifyBudget);

            notifyBudgetService.removeNotifyBudget(notifyBudget.getId());
            verify(notifyBudgetDao, times(1)).remove(notifyBudget);
        }
    }

    @Test
    public void update_mockTest_success() throws Exception {
        NotifyBudget notifyBudget = Generator.generateDefaultBudgetNotify();
        notifyBudget.setTypeNotification(TypeNotification.BUDGET_AMOUNT);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(notifyBudgetDao.find(notifyBudget.getId())).thenReturn(notifyBudget);

            notifyBudgetService.updateNotifyBudget(notifyBudget.getId(), notifyBudget);
            verify(notifyBudgetDao, times(1)).update(notifyBudget);
        }
    }

    @Test
    public void find_mockTest_success() throws Exception {
        NotifyBudget notifyBudget = Generator.generateDefaultBudgetNotify();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(notifyBudgetDao.find(notifyBudget.getId())).thenReturn(notifyBudget);

            NotifyBudget founded = notifyBudgetService.getByIdNotifyBudget(notifyBudget.getId());
            verify(notifyBudgetDao, times(1)).find(notifyBudget.getId());
            assertEquals(notifyBudget, founded);
        }
    }
}
