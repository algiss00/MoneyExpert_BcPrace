package cz.cvut.fel.controller;

import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.model.NotifyDebt;
import cz.cvut.fel.service.NotifyDebtService;
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
@RequestMapping("/notify-debt")
public class NotifyDebtController {
    private final NotifyDebtService notifyDebtService;
    private static final Logger log = Logger.getLogger(NotifyDebtController.class.getName());

    public NotifyDebtController(NotifyDebtService notifyDebtService) {
        this.notifyDebtService = notifyDebtService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllNotifyDebts() {
        List<NotifyDebt> notifyDebts = notifyDebtService.getAll();
        return new ResponseEntity<>(notifyDebts, HttpStatus.OK);
    }

    @GetMapping(value = "/debt/{debtId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getDebtsNotifyDebts(@PathVariable int debtId) throws Exception {
        List<NotifyDebt> notifyDebts = notifyDebtService.getDebtsNotifyDebts(debtId);
        return new ResponseEntity<>(notifyDebts, HttpStatus.OK);
    }

    @GetMapping(value = "/bank-account/{bankAccId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getNotifyDebtsFromBankAccount(@PathVariable int bankAccId) throws Exception {
        List<NotifyDebt> notifyDebts = notifyDebtService.getNotifyDebtsFromBankAccount(bankAccId);
        return new ResponseEntity<>(notifyDebts, HttpStatus.OK);
    }

    @GetMapping(value = "/debt-by-type/{debtId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getDebtsNotifyDebtsByType(@PathVariable int debtId, @RequestParam TypeNotification type) throws Exception {
        List<NotifyDebt> notifyDebts = notifyDebtService.getDebtsNotifyDebtsByType(debtId, type);
        return new ResponseEntity<>(notifyDebts, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getById(@PathVariable int id) throws NotifyDebtNotFoundException {
        NotifyDebt notifyDebt = notifyDebtService.getByIdNotifyDebt(id);
        return new ResponseEntity<>(notifyDebt, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> remove(@PathVariable int id) throws Exception {
        notifyDebtService.removeNotifyDebt(id);
        return new ResponseEntity<>(HttpStatus.OK);
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
                "REST /notify-debt... returned error: " + exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
