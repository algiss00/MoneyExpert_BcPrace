package cz.cvut.fel.controller;

import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Debt;
import cz.cvut.fel.model.Transaction;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.BankAccountService;
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
@RequestMapping("/account")
public class BankAccountController {
    private final BankAccountService bankAccountService;
    private static final Logger log = Logger.getLogger(User.class.getName());

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAccountById(@PathVariable int id) throws BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        BankAccount u = bankAccountService.getById(id);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @GetMapping(value = "/budgets/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllAccBudgets(@PathVariable int accId) throws BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        return new ResponseEntity<>(bankAccountService.getAllAccountsBudgets(accId), HttpStatus.OK);
    }

    @GetMapping(value = "/transactions/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllTransFromAcc(@PathVariable int accId) throws BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        List<Transaction> transactions = bankAccountService.getAllTransactions(accId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/debts/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllAccountsDebts(@PathVariable int accId) throws BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        List<Debt> d = bankAccountService.getAllAccountsDebts(accId);
        return new ResponseEntity<>(d, HttpStatus.OK);
    }

    //All my accounts
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllUserAcc() throws UserNotFoundException {
        List<BankAccount> u = bankAccountService.getAllUsersAccounts(SecurityUtils.getCurrentUser().getId());
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @GetMapping(value = "/owners", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getOwners(@RequestParam int accId) throws BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        List<User> u = bankAccountService.getAllOwners(accId);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BankAccount> add(@RequestBody BankAccount b) throws UserNotFoundException {
        if (!bankAccountService.persist(b, SecurityUtils.getCurrentUser().getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(b, HttpStatus.CREATED);
    }

    @PostMapping(value = "/owner", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BankAccount> addNewOwner(@RequestParam int userId, @RequestParam int accId) throws BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        bankAccountService.addNewOwner(userId, accId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BankAccount> updateAcc(@RequestParam int accId, @RequestBody BankAccount bankAccount) throws Exception {
        return new ResponseEntity<>(bankAccountService.updateAccount(accId, bankAccount), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> remove(@PathVariable int id) throws BankAccountNotFoundException, NotAuthenticatedClient, UserNotFoundException {
        bankAccountService.remove(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/owner")
    ResponseEntity<Void> removeOwner(@RequestParam int userId, @RequestParam int accId) throws BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        bankAccountService.removeOwner(userId, accId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/transactions")
    ResponseEntity<Void> removeAllTransactions(@RequestParam int accId) throws BankAccountNotFoundException, TransactionNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        bankAccountService.removeAllTrans(accId);
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
