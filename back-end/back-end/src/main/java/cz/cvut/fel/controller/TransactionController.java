package cz.cvut.fel.controller;

import cz.cvut.fel.model.Transaction;
import cz.cvut.fel.model.TypeTransaction;
import cz.cvut.fel.model.User;
import cz.cvut.fel.service.BankAccountService;
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
    private static final Logger log = Logger.getLogger(User.class.getName());

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    //todo
//    @GetMapping(value = "/category", produces = MediaType.APPLICATION_JSON_VALUE)
//    ResponseEntity<?> getAllTransFromCategoryFromBankAcc(@RequestParam int catId, @RequestParam int accId) throws BankAccountNotFoundException, CategoryNotFoundException {
//        List<Transaction> transactions = transactionService.getAllTransFromCategoryFromBankAcc(catId, accId);
//        return new ResponseEntity<>(transactions, HttpStatus.OK);
//    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getTransactionById(@PathVariable int id) throws TransactionNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        Transaction u = transactionService.getById(id);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Transaction> add(@RequestParam int accId, @RequestParam int categoryId, @RequestBody Transaction transaction) throws
            BankAccountNotFoundException, CategoryNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        if (!transactionService.persist(transaction, accId, categoryId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PostMapping(value = "/transfer")
    ResponseEntity<Transaction> transferTransaction(@RequestParam int fromAccId, @RequestParam int toAccId, @RequestParam int transId) throws
            BankAccountNotFoundException, TransactionNotFoundException, UserNotFoundException, NotAuthenticatedClient {
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


    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> remove(@PathVariable int id) throws TransactionNotFoundException, NotAuthenticatedClient, UserNotFoundException {
        transactionService.remove(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/category")
    ResponseEntity<Void> removeFromCategory(@RequestParam int transId) throws CategoryNotFoundException, TransactionNotFoundException, UserNotFoundException, NotAuthenticatedClient {
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
            Exception.class})
    void handleExceptions(HttpServletResponse response, Exception exception)
            throws IOException {
        log.info(() ->
                "REST /account... returned error: " + exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
