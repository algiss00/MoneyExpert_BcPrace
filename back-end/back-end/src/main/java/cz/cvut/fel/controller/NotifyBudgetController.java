package cz.cvut.fel.controller;

import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.model.NotifyBudget;
import cz.cvut.fel.service.NotifyBudgetService;
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
@RequestMapping("/notify-budget")
public class NotifyBudgetController {
    private final NotifyBudgetService notifyBudgetService;
    private static final Logger log = Logger.getLogger(NotifyBudgetController.class.getName());

    public NotifyBudgetController(NotifyBudgetService notifyBudgetService) {
        this.notifyBudgetService = notifyBudgetService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllNotifyBudgets() {
        List<NotifyBudget> notifyBudgetList = notifyBudgetService.getAll();
        return new ResponseEntity<>(notifyBudgetList, HttpStatus.OK);
    }

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getUsersNotifyBudgets() throws NotAuthenticatedClient {
        List<NotifyBudget> notifyBudget = notifyBudgetService.getUsersNotifyBudgets();
        return new ResponseEntity<>(notifyBudget, HttpStatus.OK);
    }

    @GetMapping(value = "/user-by-type", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getUsersNotifyBudgetsByType(@RequestParam TypeNotification type) throws NotAuthenticatedClient {
        List<NotifyBudget> notifyBudget = notifyBudgetService.getUsersNotifyBudgetsByType(type);
        return new ResponseEntity<>(notifyBudget, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getById(@PathVariable int id) throws NotifyBudgetNotFoundException {
        NotifyBudget notifyBudget = notifyBudgetService.getByIdNotifyBudget(id);
        return new ResponseEntity<>(notifyBudget, HttpStatus.OK);
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
                "REST /notify-budget... returned error: " + exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
