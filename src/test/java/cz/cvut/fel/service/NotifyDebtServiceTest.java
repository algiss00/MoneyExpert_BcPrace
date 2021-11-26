package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.model.NotifyDebt;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
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

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
public class NotifyDebtServiceTest {
    private NotifyDebtService notifyDebtService;
    private NotifyDebtDao notifyDebtDao;
    private User user = Generator.generateDefaultUser();
    private UserDao userDao;

    @MockBean
    private SessionRepository sessionRepository;

    @BeforeEach
    public void setUp() {
        notifyDebtDao = mock(NotifyDebtDao.class);
        TransactionDao transactionDao = mock(TransactionDao.class);
        BankAccountDao bankAccountDao = mock(BankAccountDao.class);
        BudgetDao budgetDao = mock(BudgetDao.class);
        CategoryDao categoryDao = mock(CategoryDao.class);
        DebtDao debtDao = mock(DebtDao.class);
        NotifyBudgetDao notifyBudgetDao = mock(NotifyBudgetDao.class);
        userDao = mock(UserDao.class);

        notifyDebtService = new NotifyDebtService(userDao, bankAccountDao, transactionDao,
                budgetDao, debtDao, categoryDao, notifyBudgetDao, notifyDebtDao);
    }

    @Test
    public void persist_mockTest_success() throws Exception {
        NotifyDebt notifyDebt = Generator.generateDefaultDebtNotify();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(notifyDebtDao.persist(notifyDebt)).thenReturn(notifyDebt);

            NotifyDebt persisted = notifyDebtService.persistNotifyDebt(notifyDebt);
            verify(notifyDebtDao, times(1)).persist(notifyDebt);
            assertEquals(notifyDebt, persisted);
        }
    }

    @Test
    public void remove_mockTest_success() throws Exception {
        NotifyDebt notifyDebt = Generator.generateDefaultDebtNotify();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(notifyDebtDao.find(notifyDebt.getId())).thenReturn(notifyDebt);

            notifyDebtService.removeNotifyDebt(notifyDebt.getId());
            verify(notifyDebtDao, times(1)).remove(notifyDebt);
        }
    }

    @Test
    public void update_mockTest_success() throws Exception {
        NotifyDebt notifyDebt = Generator.generateDefaultDebtNotify();
        notifyDebt.setTypeNotification(TypeNotification.DEBT_NOTIFY);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(notifyDebtDao.find(notifyDebt.getId())).thenReturn(notifyDebt);

            notifyDebtService.updateNotifyDebt(notifyDebt.getId(), notifyDebt);
            verify(notifyDebtDao, times(1)).update(notifyDebt);
        }
    }

    @Test
    public void find_mockTest_success() throws Exception {
        NotifyDebt notifyDebt = Generator.generateDefaultDebtNotify();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(notifyDebtDao.find(notifyDebt.getId())).thenReturn(notifyDebt);

            NotifyDebt founded = notifyDebtService.getByIdNotifyDebt(notifyDebt.getId());
            verify(notifyDebtDao, times(1)).find(notifyDebt.getId());
            assertEquals(notifyDebt, founded);
        }
    }
}
