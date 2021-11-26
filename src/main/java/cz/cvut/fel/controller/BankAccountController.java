package cz.cvut.fel.controller;

import cz.cvut.fel.dto.TypeTransaction;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Debt;
import cz.cvut.fel.model.Transaction;
import cz.cvut.fel.model.User;
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
@RequestMapping("/bank-account")
public class BankAccountController {
    private final BankAccountService bankAccountService;
    private static final Logger log = Logger.getLogger(BankAccountController.class.getName());

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAccountById(@PathVariable int id) throws Exception {
        BankAccount bankAccount = bankAccountService.getByIdBankAccount(id);
        return new ResponseEntity<>(bankAccount, HttpStatus.OK);
    }

    @GetMapping(value = "/available-by-name", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getByNameAvailableBankAcc(@RequestParam String name) throws Exception {
        List<BankAccount> bankAccountList = bankAccountService.getByNameAvailableBankAcc(name);
        return new ResponseEntity<>(bankAccountList, HttpStatus.OK);
    }

    @GetMapping(value = "/created-by-name", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getByNameCreatedBankAcc(@RequestParam String name) throws Exception {
        List<BankAccount> bankAccountList = bankAccountService.getByNameCreatedBankAcc(name);
        return new ResponseEntity<>(bankAccountList, HttpStatus.OK);
    }

    @GetMapping(value = "/budgets/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllAccBudgets(@PathVariable int accId) throws Exception {
        return new ResponseEntity<>(bankAccountService.getAllAccountsBudgets(accId), HttpStatus.OK);
    }

    @GetMapping(value = "/transactions/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllTransactionsFromBankAcc(@PathVariable int accId) throws Exception {
        List<Transaction> transactions = bankAccountService.getAllTransactions(accId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/transactions-by-type/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllTransFromAccByType(@PathVariable int accId, @RequestParam TypeTransaction type) throws Exception {
        List<Transaction> transactions = bankAccountService.getAllTransactionsByType(accId, type);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/transactions-by-category/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllTransactionsByCategory(@PathVariable int accId, @RequestParam int categoryId) throws Exception {
        List<Transaction> transactions = bankAccountService.getAllTransactionsByCategory(accId, categoryId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/transactions-by-category-type/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllTransactionsByCategoryAndType(@PathVariable int accId, @RequestParam int categoryId,
                                                          @RequestParam TypeTransaction type) throws Exception {
        List<Transaction> transactions = bankAccountService.getAllTransactionsByCategoryAndType(accId, categoryId, type);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping(value = "/debts/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllAccountsDebts(@PathVariable int accId) throws Exception {
        List<Debt> d = bankAccountService.getAllAccountsDebts(accId);
        return new ResponseEntity<>(d, HttpStatus.OK);
    }


    @GetMapping(value = "/creator/{bankAccId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getCreator(@PathVariable int bankAccId) throws Exception {
        User user = bankAccountService.getCreator(bankAccId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(value = "/owners/{accId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getOwners(@PathVariable int accId) throws Exception {
        List<User> u = bankAccountService.getAllOwners(accId);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BankAccount> add(@RequestBody BankAccount b) throws Exception {
        BankAccount bankAccount = bankAccountService.persist(b);
        if (bankAccount == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(bankAccount, HttpStatus.CREATED);
    }

    @PostMapping(value = "/owner", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BankAccount> addNewOwner(@RequestParam String username, @RequestParam int accId) throws Exception {
        bankAccountService.addNewOwner(username, accId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BankAccount> updateAcc(@RequestParam int accId, @RequestBody BankAccount bankAccount) throws Exception {
        return new ResponseEntity<>(bankAccountService.updateAccount(accId, bankAccount), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> remove(@PathVariable int id) throws Exception {
        bankAccountService.removeBankAcc(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/budget")
    ResponseEntity<Void> removeBudgetFromAcc(@RequestParam int budId, @RequestParam int bankAcc) throws Exception {
        bankAccountService.removeBudgetFromBankAcc(budId, bankAcc);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/owner")
    ResponseEntity<Void> removeOwner(@RequestParam int userId, @RequestParam int accId) throws Exception {
        bankAccountService.removeOwner(userId, accId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/transaction")
    ResponseEntity<Void> removeFromAccount(@RequestParam int transId) throws Exception {
        bankAccountService.removeTransactionFromBankAccount(transId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping(value = "debt")
    ResponseEntity<Void> removeDebt(@RequestParam int debtId, @RequestParam int accId) throws Exception {
        bankAccountService.removeDebt(debtId, accId);
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
                "REST /account... returned error: " + exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
