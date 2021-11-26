//package cz.cvut.fel.controller;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import cz.cvut.fel.MoneyExpertApplication;
//import cz.cvut.fel.dao.*;
//import cz.cvut.fel.model.User;
//import cz.cvut.fel.service.*;
//import generator.Generator;
//import org.junit.jupiter.api.Test;
//
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//
//@ExtendWith(SpringExtension.class)
//@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
//@WebMvcTest(UserController.class)
//public class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private BankAccountController bankAccountController;
//
//    @MockBean
//    private BudgetController budgetController;
//
//    @MockBean
//    private CategoryController categoryController;
//
//    @MockBean
//    private DebtController debtController;
//
//    @MockBean
//    private NotifyBudgetController notifyBudgetController;
//
//    @MockBean
//    private NotifyDebtController notifyDebtController;
//
//    @MockBean
//    private TransactionController transactionController;
//
//
//    @MockBean
//    private UserService userService;
//
//    @MockBean
//    private BankAccountService bankAccountService;
//
//    @MockBean
//    private BudgetService budgetService;
//
//    @MockBean
//    private CategoryService categoryService;
//
//    @MockBean
//    private DebtService debtService;
//
//    @MockBean
//    private NotifyBudgetService notifyBudgetService;
//
//    @MockBean
//    private NotifyDebtService notifyDebtService;
//
//    @MockBean
//    private TransactionService transactionService;
//
//    @MockBean
//    private UserDao userDao;
//
//    @MockBean
//    private BankAccountDao bankAccountDao;
//
//    @MockBean
//    private BudgetDao budgetDao;
//
//    @MockBean
//    private CategoryDao categoryDao;
//
//    @MockBean
//    private DebtDao debtDao;
//
//    @MockBean
//    private NotifyBudgetDao notifyBudgetDao;
//
//    @MockBean
//    private NotifyDebtDao notifyDebtDao;
//
//    @MockBean
//    private TransactionDao transactionDao;
//
//    @Test
//    public void shouldReturnDefaultMessage() throws Exception {
//        User user = Generator.generateDefaultUser();
//        when(userService.getAuthenticatedUser()).thenReturn(user);
//        this.mockMvc.perform(get("/user/current-user")).andExpect(status().isOk());
//    }
//}
//
