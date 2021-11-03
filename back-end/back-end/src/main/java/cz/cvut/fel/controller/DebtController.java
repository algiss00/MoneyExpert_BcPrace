package cz.cvut.fel.controller;

import cz.cvut.fel.model.Debt;
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
    private static final Logger log = Logger.getLogger(DebtController.class.getName());

    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getDebtById(@PathVariable int id) throws Exception {
        Debt d = debtService.getByIdDebt(id);
        return new ResponseEntity<>(d, HttpStatus.OK);
    }

    @GetMapping(value = "/by-name/{bankAccId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getByNameFromBankAcc(@PathVariable int bankAccId, @RequestParam String name) throws Exception {
        List<Debt> d = debtService.getByNameFromBankAcc(bankAccId, name);
        return new ResponseEntity<>(d, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Debt> add(@RequestBody Debt d, @RequestParam int accId) throws Exception {
        Debt debt = debtService.persist(d, accId);
        if (debt == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(debt, HttpStatus.CREATED);
    }
    
    @PostMapping(value = "/update-basic", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Debt> updateDebt(@RequestParam int debtId, @RequestBody Debt debt) throws Exception {
        return new ResponseEntity<>(debtService.updateDebtBasic(debtId, debt), HttpStatus.CREATED);
    }

    @PostMapping(value = "/update-notify-date", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Debt> updateNotifyDate(@RequestParam int debtId, @RequestParam String notifyDate) throws Exception {
        return new ResponseEntity<>(debtService.updateDebtNotifyDate(debtId, notifyDate), HttpStatus.CREATED);
    }

    @PostMapping(value = "/update-deadline-date", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Debt> updateDeadlineDate(@RequestParam int debtId, @RequestParam String deadline) throws Exception {
        return new ResponseEntity<>(debtService.updateDebtDeadlineDate(debtId, deadline), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> remove(@PathVariable int id) throws Exception {
        debtService.removeDebt(id);
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
                "REST /debt... returned error: " + exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
