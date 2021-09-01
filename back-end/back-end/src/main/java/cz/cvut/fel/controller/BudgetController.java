package cz.cvut.fel.controller;

import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.BankAccountService;
import cz.cvut.fel.service.BudgetService;
import cz.cvut.fel.service.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/budget")
public class BudgetController {
    private final BudgetService budgetService;
    private final BankAccountService bankAccountService;
    private static final Logger log = Logger.getLogger(User.class.getName());

    public BudgetController(BudgetService budgetService, BankAccountService bankAccountService) {
        this.budgetService = budgetService;
        this.bankAccountService = bankAccountService;
    }

//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    ResponseEntity<?> getAllBudgets() {
//        return new ResponseEntity<>(budgetService.getAll(), HttpStatus.OK);
//    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getBudgetById(@PathVariable int id) throws BudgetNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        return new ResponseEntity<>(budgetService.getById(id), HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Budget> add(@RequestParam int accId, @RequestParam int categoryId, @RequestBody Budget b) throws UserNotFoundException,
            BankAccountNotFoundException, CategoryNotFoundException, NotAuthenticatedClient {
        if (!budgetService.persist(b, accId, categoryId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(b, HttpStatus.CREATED);
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Budget> updateBudget(@RequestParam int budId, @RequestBody Budget budget) throws BudgetNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        return new ResponseEntity<>(budgetService.updateBudget(budId, budget), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> remove(@PathVariable int id) throws UserNotFoundException, BudgetNotFoundException, NotAuthenticatedClient {
        if (budgetService.remove(id)) {
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/account")
    ResponseEntity<Void> removeBudgetFromAcc(@RequestParam int budId, @RequestParam int bankAcc) throws BankAccountNotFoundException, BudgetNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        bankAccountService.removeBudgetFromBankAcc(budId, bankAcc);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/category")
    ResponseEntity<Void> removeCategoryFromBudget(@RequestParam int budId) throws BudgetNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        budgetService.removeCategoryFromBudget(budId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ExceptionHandler({
            TransactionNotFoundException.class,
            BankAccountNotFoundException.class,
            UserNotFoundException.class,
            BudgetNotFoundException.class,
            CategoryNotFoundException.class,
            DebtNotFoundException.class,
            NotAuthenticatedClient.class,
            Exception.class})
    void handleExceptions(HttpServletResponse response, Exception exception)
            throws IOException {
        log.info(() ->
                "REST /account... returned error: " + exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
