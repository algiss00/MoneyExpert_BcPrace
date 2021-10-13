package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Category;
import cz.cvut.fel.model.Debt;
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
public class DebtServiceTest {
    private DebtService debtService;
    private DebtDao debtDao;
    private User user = Generator.generateDefaultUser();
    private UserDao userDao;
    private BankAccountDao bankAccountDao;

    @BeforeEach
    public void setUp() {
        debtDao = mock(DebtDao.class);
        TransactionDao transactionDao = mock(TransactionDao.class);
        bankAccountDao = mock(BankAccountDao.class);
        BudgetDao budgetDao = mock(BudgetDao.class);
        CategoryDao categoryDao = mock(CategoryDao.class);
        NotifyBudgetDao notifyBudgetDao = mock(NotifyBudgetDao.class);
        NotifyDebtDao notifyDebtDao = mock(NotifyDebtDao.class);
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
            when(bankAccountDao.getUsersBankAccountById(anyInt(), anyInt())).thenReturn(bankAccount);
            when(debtDao.persist(debt)).thenReturn(debt);
            when(debtDao.getByName(user.getId(), debt.getName())).thenReturn(null);

            Debt persisted = debtService.persist(debt, bankAccount.getId());
            verify(debtDao, times(1)).persist(debt);
            assertEquals(debt, persisted);
        }
    }

    @Test
    public void remove_mockTest_success() throws Exception {
        Debt debt = Generator.generateDefaultDebt();
        debt.setCreator(user);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(debtDao.find(debt.getId())).thenReturn(debt);

            debtService.remove(debt.getId());
            verify(debtDao, times(1)).remove(debt);
        }
    }

    @Test
    public void update_mockTest_success() throws Exception {
        Debt debt = Generator.generateDefaultDebt();
        debt.setCreator(user);
        debt.setName("mock test");
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(debtDao.find(debt.getId())).thenReturn(debt);
            when(debtDao.update(debt)).thenReturn(debt);

            Debt updated = debtService.updateDebt(debt.getId(), debt);
            verify(debtDao, times(1)).update(debt);
            assertEquals(debt, updated);
        }
    }

    @Test
    public void find_mockTest_success() throws Exception {
        Debt debt = Generator.generateDefaultDebt();
        debt.setCreator(user);
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            HelperFunctions.authUser(utilities, userDao, user);
            when(debtDao.find(debt.getId())).thenReturn(debt);

            Debt founded = debtService.getByIdDebt(debt.getId());
            verify(debtDao, times(1)).find(debt.getId());
            assertEquals(debt, founded);
        }
    }
}
