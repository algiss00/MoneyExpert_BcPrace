package cz.cvut.fel.controller;

import cz.cvut.fel.model.Debt;
import cz.cvut.fel.model.User;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.DebtService;
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
@RequestMapping("/debt")
public class DebtController {
    private final DebtService debtService;
    private static final Logger log = Logger.getLogger(User.class.getName());

    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    ResponseEntity<?> getAllDebts() {
//        List<Debt> cateAll = debtService.getAll();
//        return new ResponseEntity<>(cateAll, HttpStatus.OK);
//    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getDebtById(@PathVariable int id) throws DebtNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        Debt d = debtService.getById(id);
        return new ResponseEntity<>(d, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Debt> add(@RequestBody Debt d, @RequestParam int accId) throws UserNotFoundException, BankAccountNotFoundException, NotAuthenticatedClient {
        if (!debtService.persist(d, accId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(d, HttpStatus.CREATED);
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Debt> updateDebt(@RequestParam int debtId, @RequestBody Debt debt) throws DebtNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        return new ResponseEntity<>(debtService.updateDebt(debtId, debt), HttpStatus.CREATED);
    }

//    @PostMapping(value = "/check-debts")
//    ResponseEntity<Void> asyncFuncCheckDebts() throws UserNotFoundException, InterruptedException, NotAuthenticatedClient {
//        debtService.asyncMethodCheckingDebts();
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> remove(@PathVariable int id) throws DebtNotFoundException, NotAuthenticatedClient, UserNotFoundException {
        debtService.remove(id);
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
