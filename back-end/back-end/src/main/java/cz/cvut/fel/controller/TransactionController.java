package cz.cvut.fel.controller;

import cz.cvut.fel.dto.SortAttribute;
import cz.cvut.fel.dto.SortOrder;
import cz.cvut.fel.model.Transaction;
import cz.cvut.fel.dto.TypeTransaction;
import cz.cvut.fel.service.TransactionService;
import cz.cvut.fel.service.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;
    private static final Logger log = Logger.getLogger(TransactionController.class.getName());

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(value = "/between-date/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getBetweenDate(@RequestParam String from, @RequestParam String to, @PathVariable int accId) throws Exception {
        List<Transaction> transactions = transactionService.getBetweenDate(from, to, accId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/category", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllTransFromCategoryFromBankAcc(@RequestParam int catId, @RequestParam int accId) throws Exception {
        List<Transaction> transactions = transactionService.getAllTransFromCategoryFromBankAcc(catId, accId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getTransactionById(@PathVariable int id) throws Exception {
        Transaction u = transactionService.getByIdTransaction(id);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @GetMapping(value = "/sorted-transactions-month/{bankAccId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getTransactionSortedByMonth(@PathVariable int bankAccId, @RequestParam int month)
            throws Exception {
        List<Transaction> transactions = transactionService.getSortedByMonth(month, bankAccId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/sum-expense/{bankAccId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getSumExpenseInMonth(@PathVariable int bankAccId, @RequestParam int month)
            throws Exception {
        double transactions = transactionService.getSumOfExpenseOnMonth(month, bankAccId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/sum-income/{bankAccId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getSumIncomeInMonth(@PathVariable int bankAccId, @RequestParam int month)
            throws Exception {
        double transactions = transactionService.getSumOfIncomeOnMonth(month, bankAccId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/sum-expense-category/{bankAccId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getSumExpenseWithCategory(@PathVariable int bankAccId, @RequestParam int month, @RequestParam int categoryId)
            throws Exception {
        double transactions = transactionService.getSumOfExpenseWithCategory(month, bankAccId, categoryId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/sum-income-category/{bankAccId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getSumIncomeWithCategory(@PathVariable int bankAccId, @RequestParam int month, @RequestParam int categoryId)
            throws Exception {
        double transactions = transactionService.getSumOfIncomeWithCategory(month, bankAccId, categoryId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Transaction> add(@RequestParam int accId, @RequestParam int categoryId, @RequestBody Transaction transaction) throws
            Exception {
        if (!transactionService.persist(transaction, accId, categoryId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PostMapping(value = "/transfer")
    ResponseEntity<Transaction> transferTransaction(@RequestParam int fromAccId, @RequestParam int toAccId, @RequestParam int transId)
            throws Exception {
        Transaction t = transactionService.transferTransaction(fromAccId, toAccId, transId);
        return new ResponseEntity<>(t, HttpStatus.CREATED);
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Transaction> updateTrans(@RequestParam int transId, @RequestBody Transaction transaction) throws Exception {
        return new ResponseEntity<>(transactionService.update(transId, transaction), HttpStatus.CREATED);
    }

    @PostMapping(value = "/update-type", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Transaction> updateTransType(@RequestParam int transId, @RequestBody TypeTransaction typeTransaction) throws Exception {
        return new ResponseEntity<>(transactionService.updateTransactionType(transId, typeTransaction), HttpStatus.CREATED);
    }

    @PostMapping(value = "/update-category", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Transaction> updateCategory(@RequestParam int transId, @RequestBody int catId) throws Exception {
        return new ResponseEntity<>(transactionService.updateCategory(transId, catId), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> remove(@PathVariable int id) throws Exception {
        transactionService.remove(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/category")
    ResponseEntity<Void> removeFromCategory(@RequestParam int transId) throws Exception {
        transactionService.removeFromCategory(transId);
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
            Exception.class})
    void handleExceptions(HttpServletResponse response, Exception exception)
            throws IOException {
        log.info(() ->
                "REST /transaction... returned error: " + exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
