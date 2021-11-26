package cz.cvut.fel.controller;

import cz.cvut.fel.model.Budget;
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
    private static final Logger log = Logger.getLogger(BudgetController.class.getName());

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getBudgetById(@PathVariable int id) throws Exception {
        return new ResponseEntity<>(budgetService.getByIdBudget(id), HttpStatus.OK);
    }

    @GetMapping(value = "/budget-category/{budgetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getCategoryOfBudget(@PathVariable int budgetId) throws Exception {
        return new ResponseEntity<>(budgetService.getCategoryOfBudget(budgetId), HttpStatus.OK);
    }

    @GetMapping(value = "/budget-transactions/{budgetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getTransactionFromBudget(@PathVariable int budgetId) throws Exception {
        return new ResponseEntity<>(budgetService.getTransactionsFromBudget(budgetId), HttpStatus.OK);
    }

    @GetMapping(value = "/name/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getBudgetByName(@RequestParam String name, @PathVariable int accId) throws Exception {
        return new ResponseEntity<>(budgetService.getByName(accId, name), HttpStatus.OK);
    }

    @GetMapping(value = "/category/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getByCategory(@RequestParam int categoryId, @PathVariable int accId) throws Exception {
        return new ResponseEntity<>(budgetService.getBudgetByCategoryInBankAcc(categoryId, accId), HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Budget> add(@RequestParam int accId, @RequestParam int categoryId, @RequestBody Budget b) throws
            Exception {
        Budget budget = budgetService.persist(b, accId, categoryId);
        if (budget == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(budget, HttpStatus.CREATED);
    }

    @PostMapping(value = "/update-name", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Budget> updateBudget(@RequestParam int budId, @RequestParam String name) throws Exception {
        return new ResponseEntity<>(budgetService.updateBudgetName(budId, name), HttpStatus.CREATED);
    }

    @PostMapping(value = "/update-amount", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Budget> updateBudgetAmount(@RequestParam int budId, @RequestParam Double amount) throws Exception {
        return new ResponseEntity<>(budgetService.updateBudgetAmount(budId, amount), HttpStatus.CREATED);
    }

    @PostMapping(value = "/update-percent", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Budget> updateBudgetPercent(@RequestParam int budId, @RequestParam int percent) throws Exception {
        return new ResponseEntity<>(budgetService.updateBudgetPercent(budId, percent), HttpStatus.CREATED);
    }

    @PostMapping(value = "/update-category", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Budget> updateBudgetCategory(@RequestParam int budId, @RequestParam int categoryId) throws Exception {
        return new ResponseEntity<>(budgetService.updateBudgetCategory(budId, categoryId), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> remove(@PathVariable int id) throws Exception {
        budgetService.removeBudget(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/category")
    ResponseEntity<Void> removeCategoryFromBudget(@RequestParam int budId) throws Exception {
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
            NotifyBudgetNotFoundException.class,
            NotifyDebtNotFoundException.class,
            NotValidDataException.class,
            Exception.class})
    void handleExceptions(HttpServletResponse response, Exception exception)
            throws IOException {
        log.info(() ->
                "REST /budget... returned error: " + exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
