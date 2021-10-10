package cz.cvut.fel.service;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.User;
import generator.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@ExtendWith(SpringExtension.class)
//@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
//public class BankAccountServiceTest {
//    private BankAccountService bankAccountService;
//    private BankAccountDao bankAccountDao;
//
//    @BeforeEach
//    public void setUp() {
//        bankAccountDao = mock(BankAccountDao.class);
//        BankAccountDao bankAccountDao = mock(BankAccountDao.class);
//        TransactionDao transactionDao = mock(TransactionDao.class);
//        BudgetDao budgetDao = mock(BudgetDao.class);
//        DebtDao debtDao = mock(DebtDao.class);
//        CategoryDao categoryDao = mock(CategoryDao.class);
//        NotifyBudgetDao notifyBudgetDao = mock(NotifyBudgetDao.class);
//        NotifyDebtDao notifyDebtDao = mock(NotifyDebtDao.class);
//        UserDao userDao = mock(UserDao.class);
//
//        bankAccountService = new BankAccountService(userDao, bankAccountDao, transactionDao,
//                budgetDao, debtDao, categoryDao,
//                notifyBudgetDao, notifyDebtDao);
//    }
//
//    @Test
//    public void persist_mockTest_success() throws Exception {
//        BankAccount bankAccount = Generator.generateDefaultBankAccount();
//        doReturn(new User()).when((AbstractServiceHelper) bankAccountService).isLogged();
//        doReturn(bankAccount).when(bankAccountService).persist(bankAccount);
//        doNothing().when(bankAccountService).createStartTransaction(any());
//        BankAccount persisted = bankAccountService.persist(bankAccount);
//        assertEquals(bankAccount, persisted);
//    }
//
//    @Test
//    public void remove_MockTest_success() throws Exception {
//        BankAccount bankAccount = Generator.generateDefaultBankAccount();
//        doReturn(bankAccount).when((AbstractServiceHelper) bankAccountService).getByIdBankAccount(anyInt());
//
//        bankAccountService.remove(bankAccount.getId());
//        verify(bankAccountDao, times(1)).remove(bankAccount);
//    }
//}
