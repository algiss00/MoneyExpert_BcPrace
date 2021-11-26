package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.BankAccount;
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
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.SessionRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
public class UserServiceTest {

    private UserService userService;
    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private UserDao userDao;

    @MockBean
    private SessionRepository sessionRepository;

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
        user.setId(1);

        User user2 = Generator.generateDefaultUser();
        user2.setId(2);
        user2.setName("test name");
        user2.setLastname("test lastname");
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(user);
            assertEquals(user, SecurityUtils.getCurrentUser());
            when(userDao.find(user.getId())).thenReturn(user);

            userService.updateUserBasic(user2);
            assertEquals("test lastname", user.getLastname());
            assertEquals("test name", user.getName());
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

    /**
     * get all users bankAccounts, including available and created bankAccounts
     *
     * @throws Exception
     */
    @Test
    public void getAllBankAccounts_mockTest_success() throws Exception {
        User user = Generator.generateDefaultUser();
        user.setId(1);

        User user2 = Generator.generateDefaultUser();
        user2.setId(3);


        BankAccount createdBank = Generator.generateDefaultBankAccount();
        createdBank.setId(2);
        createdBank.setCreator(user);
        user.getCreatedBankAccounts().add(createdBank);

        BankAccount availableBank = Generator.generateDefaultBankAccount();
        availableBank.setId(3);
        availableBank.setCreator(user2);

        availableBank.getOwners().add(user);
        user.getAvailableBankAccounts().add(availableBank);


        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(user);
            when(userDao.find(anyInt())).thenReturn(user);

            List<BankAccount> returned = userService.getAllUsersBankAccounts();
            assertEquals(2, returned.size());
        }
    }
}
