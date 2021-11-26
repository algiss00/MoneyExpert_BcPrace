package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Debt;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
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

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
public class DebtServiceTest {
    private DebtService debtService;
    private DebtDao debtDao;
    private User user = Generator.generateDefaultUser();
    private UserDao userDao;
    private BankAccountDao bankAccountDao;
    private NotifyDebtDao notifyDebtDao;

    @MockBean
    private SessionRepository sessionRepository;

    @BeforeEach
    public void setUp() {
        debtDao = mock(DebtDao.class);
        TransactionDao transactionDao = mock(TransactionDao.class);
        bankAccountDao = mock(BankAccountDao.class);
        BudgetDao budgetDao = mock(BudgetDao.class);
        CategoryDao categoryDao = mock(CategoryDao.class);
        NotifyBudgetDao notifyBudgetDao = mock(NotifyBudgetDao.class);
        notifyDebtDao = mock(NotifyDebtDao.class);
        userDao = mock(UserDao.class);

        debtService = new DebtService(userDao, bankAccountDao, transactionDao,
                budgetDao, debtDao, categoryDao,
                notifyBudgetDao, notifyDebtDao);
    }

    @Test
    public void persist_mockTest_success() throws Exception {
        Debt debt = Generator.generateDefaultDebt();
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(bankAccountDao.find(anyInt())).thenReturn(bankAccount);
            when(bankAccountDao.getUsersAvailableBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
            when(debtDao.persist(debt)).thenReturn(debt);
            when(debtDao.getByNameFromBankAcc(user.getId(), debt.getName())).thenReturn(null);

            Debt persisted = debtService.persist(debt, bankAccount.getId());
            verify(debtDao, times(1)).persist(debt);
            assertEquals(debt, persisted);
        }
    }

    @Test
    public void remove_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        Debt debt = Generator.generateDefaultDebt();
        debt.setBankAccount(bankAccount);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(debtDao.find(debt.getId())).thenReturn(debt);

            debtService.removeDebt(debt.getId());
            verify(debtDao, times(1)).remove(debt);
        }
    }

    @Test
    public void updateDebtBasic_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        Debt debt = Generator.generateDefaultDebt();
        debt.setBankAccount(bankAccount);
        debt.setName("mock test");
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(debtDao.find(debt.getId())).thenReturn(debt);
            when(debtDao.update(debt)).thenReturn(debt);

            Debt updated = debtService.updateDebtBasic(debt.getId(), debt);
            verify(debtDao, times(1)).update(debt);
            assertEquals(debt, updated);
        }
    }

    @Test
    public void updateDebtNotifyDate_mockTest_success() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date notify = formatter.parse("2021-10-05");
        Date notify2 = formatter.parse("2021-11-10");
        Date deadline = formatter.parse("2021-11-20");

        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        Debt debt = Generator.generateDefaultDebt();
        debt.setBankAccount(bankAccount);
        debt.setName("mock test");
        debt.setDeadline(deadline);
        debt.setNotifyDate(notify);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(debtDao.find(debt.getId())).thenReturn(debt);
            when(debtDao.update(debt)).thenReturn(debt);
            when(notifyDebtDao.getDebtsNotifyDebtsByType(debt.getId(), TypeNotification.DEBT_NOTIFY)).thenReturn(null);

            debtService.updateDebtNotifyDate(debt.getId(), notify2);
            verify(debtDao, times(1)).update(debt);
            assertEquals(debt.getNotifyDate(), notify2);
        }
    }

    @Test
    public void updateDebtDeadlineDate_mockTest_success() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date notify = formatter.parse("2021-10-05");
        Date deadline = formatter.parse("2021-11-20");
        Date deadline2 = formatter.parse("2021-11-25");

        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        Debt debt = Generator.generateDefaultDebt();
        debt.setBankAccount(bankAccount);
        debt.setName("mock test");
        debt.setDeadline(deadline);
        debt.setNotifyDate(notify);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(debtDao.find(debt.getId())).thenReturn(debt);
            when(debtDao.update(debt)).thenReturn(debt);
            when(notifyDebtDao.getDebtsNotifyDebtsByType(debt.getId(), TypeNotification.DEBT_DEADLINE)).thenReturn(null);

            debtService.updateDebtDeadlineDate(debt.getId(), deadline2);
            verify(debtDao, times(1)).update(debt);
            assertEquals(debt.getDeadline(), deadline2);
        }
    }

    @Test
    public void find_mockTest_success() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        bankAccount.setCreator(user);
        Debt debt = Generator.generateDefaultDebt();
        debt.setBankAccount(bankAccount);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(debtDao.find(debt.getId())).thenReturn(debt);

            Debt founded = debtService.getByIdDebt(debt.getId());
            verify(debtDao, times(1)).find(debt.getId());
            assertEquals(debt, founded);
        }
    }

    @Test
    public void find_mockTest_throwNotAuthenticatedClient() throws Exception {
        BankAccount bankAccount = Generator.generateDefaultBankAccount();
        BankAccount bankAccount2 = Generator.generateDefaultBankAccount();

        bankAccount.setCreator(user);
        Debt debt = Generator.generateDefaultDebt();
        // belongs to other BankAcc
        debt.setBankAccount(bankAccount2);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(debtDao.find(debt.getId())).thenReturn(debt);

            assertThrows(NotAuthenticatedClient.class, () -> {
                debtService.getByIdDebt(debt.getId());
            });
        }
    }

    @Test
    public void checkNotifyDates_mockTest_success() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date notify = formatter.parse("2021-10-05");

        Debt debt = Generator.generateDefaultDebt();
        debt.setNotifyDate(notify);

        Debt debt1 = Generator.generateDefaultDebt();
        debt1.setNotifyDate(notify);

        Debt debt2 = Generator.generateDefaultDebt();
        debt2.setNotifyDate(notify);

        Debt debt3 = Generator.generateDefaultDebt();
        debt3.setNotifyDate(notify);

        Debt[] notifyDebts = {debt, debt1, debt2, debt3};
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(debtDao.getNotifyDebts()).thenReturn(Arrays.asList(notifyDebts));
            debtService.checkNotifyDates();
            verify(debtDao, times(1)).getNotifyDebts();
            verify(notifyDebtDao, times(4)).persist(any());
        }
    }

    @Test
    public void checkNotifyDatesEmpty_mockTest_success() throws Exception {
        Debt debt = Generator.generateDefaultDebt();
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(debtDao.find(debt.getId())).thenReturn(debt);
            when(debtDao.getNotifyDebts()).thenReturn(Collections.emptyList());
            List<Debt> notifyDebts = debtDao.getNotifyDebts();
            assertTrue(notifyDebts.isEmpty());
            debtService.checkNotifyDates();
            verify(debtDao, times(2)).getNotifyDebts();
        }
    }

    @Test
    public void checkDeadlineDates_mockTest_success() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = formatter.parse("2021-10-05");

        Debt debt = Generator.generateDefaultDebt();
        debt.setDeadline(deadline);

        Debt debt1 = Generator.generateDefaultDebt();
        debt1.setDeadline(deadline);

        Debt debt2 = Generator.generateDefaultDebt();
        debt2.setDeadline(deadline);

        Debt debt3 = Generator.generateDefaultDebt();
        debt3.setDeadline(deadline);

        Debt[] deadlineDebts = {debt, debt1, debt2, debt3};
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(debtDao.getDeadlineDebts()).thenReturn(Arrays.asList(deadlineDebts));
            debtService.checkDeadlineDates();
            verify(debtDao, times(1)).getDeadlineDebts();
            verify(notifyDebtDao, times(4)).persist(any());
        }
    }
}
