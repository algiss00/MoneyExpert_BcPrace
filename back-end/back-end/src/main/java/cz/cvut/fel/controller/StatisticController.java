//package cz.cvut.fel.controller;
//
//import cz.cvut.fel.model.BankAccount;
//import cz.cvut.fel.model.User;
//import cz.cvut.fel.service.*;
//import cz.cvut.fel.service.exceptions.*;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.logging.Logger;
//
//@RestController
//@RequestMapping("/statistic")
//public class StatisticController {
//    private final BankAccountService bankAccountService;
//    private final BudgetService budgetService;
//    private final DebtService debtService;
//    private final TransactionService transactionService;
//    private final UserService userService;
//
//    private static final Logger log = Logger.getLogger(StatisticController.class.getName());
//
//    public StatisticController(BankAccountService bankAccountService, BudgetService budgetService, DebtService debtService, TransactionService transactionService, UserService userService) {
//        this.bankAccountService = bankAccountService;
//        this.budgetService = budgetService;
//        this.debtService = debtService;
//        this.transactionService = transactionService;
//        this.userService = userService;
//    }
//
////    @GetMapping(value = "/category", produces = MediaType.APPLICATION_JSON_VALUE)
////    ResponseEntity<?> getAccountById(@RequestParam int catId) {
////
////    }
//
//    @ExceptionHandler({
//            TransactionNotFoundException.class,
//            BankAccountNotFoundException.class,
//            UserNotFoundException.class,
//            BudgetNotFoundException.class,
//            CategoryNotFoundException.class,
//            DebtNotFoundException.class,
//            NotAuthenticatedClient.class,
//            NotifyBudgetNotFoundException.class,
//            NotifyDebtNotFoundException.class,
//            Exception.class})
//    void handleExceptions(HttpServletResponse response, Exception exception)
//            throws IOException {
//        log.info(() ->
//                "REST /statistic... returned error: " + exception.getMessage());
//        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
//    }
//}
