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
import java.util.logging.Logger;

@RestController
@RequestMapping("/debt")
public class DebtController {
    private final DebtService debtService;
    private static final Logger log = Logger.getLogger(DebtController.class.getName());

    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    ResponseEntity<?> getAllDebts() {
//        List<Debt> debtAll = debtService.getAll();
//        return new ResponseEntity<>(dabtAll, HttpStatus.OK);
//    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getDebtById(@PathVariable int id) throws DebtNotFoundException, NotAuthenticatedClient {
        Debt d = debtService.getByIdDebt(id);
        return new ResponseEntity<>(d, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Debt> add(@RequestBody Debt d, @RequestParam int accId) throws Exception {
        if (!debtService.persist(d, accId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(d, HttpStatus.CREATED);
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Debt> updateDebt(@RequestParam int debtId, @RequestBody Debt debt) throws DebtNotFoundException, NotAuthenticatedClient {
        return new ResponseEntity<>(debtService.updateDebt(debtId, debt), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> remove(@PathVariable int id) throws DebtNotFoundException, NotAuthenticatedClient {
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
            NotifyBudgetNotFoundException.class,
            NotifyDebtNotFoundException.class,
            Exception.class})
    void handleExceptions(HttpServletResponse response, Exception exception)
            throws IOException {
        log.info(() ->
                "REST /debt... returned error: " + exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
