package cz.cvut.fel.controller;

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

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getById(@PathVariable int id) throws NotifyDebtNotFoundException {
        NotifyDebt notifyDebt = notifyDebtService.getByIdNotifyDebt(id);
        return new ResponseEntity<>(notifyDebt, HttpStatus.OK);
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
                "REST /account... returned error: " + exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
