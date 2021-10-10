package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import generator.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
public class UserServiceTest {

    private UserService userService;
    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        userDao = mock(UserDao.class);
        BankAccountDao bankAccountDao = mock(BankAccountDao.class);
        TransactionDao transactionDao = mock(TransactionDao.class);
        BudgetDao budgetDao = mock(BudgetDao.class);
        DebtDao debtDao = mock(DebtDao.class);
        CategoryDao categoryDao = mock(CategoryDao.class);
        NotifyBudgetDao notifyBudgetDao = mock(NotifyBudgetDao.class);
        NotifyDebtDao notifyDebtDao = mock(NotifyDebtDao.class);

        userService = new UserService(userDao, bankAccountDao, transactionDao,
                budgetDao, debtDao, categoryDao,
                notifyBudgetDao, notifyDebtDao, encoder);
    }

    @Test
    public void persist_mockTest_success() throws Exception {
        User user = Generator.generateDefaultUser();
        userService.persist(user);
        verify(userDao, times(1)).persist(user);
    }

    @Test
    public void remove_MockTest_success() throws Exception {
        User user = Generator.generateDefaultUser();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(user);
            assertEquals(user, SecurityUtils.getCurrentUser());
            when(userDao.find(anyInt())).thenReturn(user);
            userService.remove();
            verify(userDao, times(1)).remove(any());
        }
    }

    @Test
    public void update_mockTest_success() throws Exception {
        User user = Generator.generateDefaultUser();
        user.setName("mock-test");
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(user);
            assertEquals(user, SecurityUtils.getCurrentUser());
            when(userDao.find(anyInt())).thenReturn(user);
            when(userDao.update(any())).thenReturn(user);
            assertEquals(user, userService.updateUserBasic(user));
            verify(userDao, times(1)).update(user);
        }
    }

    @Test
    public void find_mockTest_success() throws Exception {
        User user = Generator.generateDefaultUser();
        when(userDao.find(anyInt())).thenReturn(user);
        User returned = userService.getByIdUser(user.getId());
        assertEquals(user, returned);
    }
}
