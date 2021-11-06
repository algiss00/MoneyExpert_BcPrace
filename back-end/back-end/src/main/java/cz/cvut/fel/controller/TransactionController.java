package cz.cvut.fel.controller;

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

    @GetMapping(value = "/sorted-type/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getTransactionsByType(@RequestParam TypeTransaction type, @PathVariable int accId,
                                            @RequestParam int month, @RequestParam int year) throws Exception {
        List<Transaction> transactions = transactionService.getTransactionsByType(accId, type, month, year);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/sorted-category/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllTransFromCategoryFromBankAcc(@RequestParam int catId, @PathVariable int accId,
                                                         @RequestParam int month, @RequestParam int year) throws Exception {
        List<Transaction> transactions = transactionService.getAllTransFromCategoryFromBankAcc(catId, accId, month, year);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/sorted-type-category/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getTransactionsByTypeAndCategory(@RequestParam int catId, @PathVariable int accId, @RequestParam TypeTransaction type,
                                                       @RequestParam int month, @RequestParam int year) throws Exception {
        List<Transaction> transactions = transactionService.getTransactionsByTypeAndCategory(catId, accId, type, month, year);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getTransactionById(@PathVariable int id) throws Exception {
        Transaction u = transactionService.getByIdTransaction(id);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @GetMapping(value = "/sorted-month-year/{bankAccId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getSortedByMonthAndYear(@PathVariable int bankAccId, @RequestParam int month, @RequestParam int year)
            throws Exception {
        List<Transaction> transactions = transactionService.getSortedByMonthAndYear(month, year, bankAccId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/sum-expense/{bankAccId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getSumExpenseInMonth(@PathVariable int bankAccId, @RequestParam int month, @RequestParam int year)
            throws Exception {
        double sum = transactionService.getSumOfExpenseOnMonth(month, year, bankAccId);
        return new ResponseEntity<>(sum, HttpStatus.OK);
    }

    @GetMapping(value = "/sum-income/{bankAccId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getSumIncomeInMonth(@PathVariable int bankAccId, @RequestParam int month, @RequestParam int year)
            throws Exception {
        double sum = transactionService.getSumOfIncomeOnMonth(month, year, bankAccId);
        return new ResponseEntity<>(sum, HttpStatus.OK);
    }

    @GetMapping(value = "/sum-expense-category/{bankAccId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getSumExpenseWithCategory(@PathVariable int bankAccId, @RequestParam int month,
                                                @RequestParam int year, @RequestParam int categoryId)
            throws Exception {
        double sum = transactionService.getSumOfExpenseWithCategory(month, year, bankAccId, categoryId);
        return new ResponseEntity<>(sum, HttpStatus.OK);
    }

    @GetMapping(value = "/sum-income-category/{bankAccId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getSumIncomeWithCategory(@PathVariable int bankAccId, @RequestParam int month,
                                               @RequestParam int year, @RequestParam int categoryId)
            throws Exception {
        double sum = transactionService.getSumOfIncomeWithCategory(month, year, bankAccId, categoryId);
        return new ResponseEntity<>(sum, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Transaction> add(@RequestParam int accId, @RequestParam int categoryId, @RequestBody Transaction transaction)
            throws Exception {
        Transaction t = transactionService.persist(transaction, accId, categoryId);
        return new ResponseEntity<>(t, HttpStatus.CREATED);
    }

    @PostMapping(value = "/transfer")
    ResponseEntity<Transaction> transferTransaction(@RequestParam int fromAccId, @RequestParam int toAccId, @RequestParam int transId)
            throws Exception {
        Transaction t = transactionService.transferTransaction(fromAccId, toAccId, transId);
        return new ResponseEntity<>(t, HttpStatus.CREATED);
    }

    @PostMapping(value = "/update-basic", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Transaction> updateTrans(@RequestParam int transId, @RequestBody Transaction transaction) throws Exception {
        return new ResponseEntity<>(transactionService.updateBasic(transId, transaction), HttpStatus.CREATED);
    }

    @PostMapping(value = "/update-type", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Transaction> updateTransType(@RequestParam int transId, @RequestParam TypeTransaction typeTransaction) throws Exception {
        return new ResponseEntity<>(transactionService.updateTransactionType(transId, typeTransaction), HttpStatus.CREATED);
    }

    @PostMapping(value = "/update-category", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Transaction> updateCategory(@RequestParam int transId, @RequestParam int catId) throws Exception {
        return new ResponseEntity<>(transactionService.updateCategoryTransaction(transId, catId), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> remove(@PathVariable int id) throws Exception {
        transactionService.remove(id);
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
                "REST /transaction... returned error: " + exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
