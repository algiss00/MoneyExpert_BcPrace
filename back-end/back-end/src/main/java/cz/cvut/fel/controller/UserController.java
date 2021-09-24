package cz.cvut.fel.controller;

import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.UserService;
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
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private static final Logger log = Logger.getLogger(UserController.class.getName());

    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    ResponseEntity<?> getAll() {
//        List<User> usersAll = userService.getAll();
//        return new ResponseEntity<>(usersAll, HttpStatus.OK);
//    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getUserById(@PathVariable int id) throws UserNotFoundException {
        User u = userService.getByIdUser(id);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @GetMapping(value = "/current-user", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getCurrentUser() throws UserNotFoundException {
        User user;
        try {
            user = userService.getByIdUser(SecurityUtils.getCurrentUser().getId());
        } catch (Exception ex) {
            throw new UserNotFoundException();
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAvailableAccounts() throws UserNotFoundException, NotAuthenticatedClient {
        List<BankAccount> accounts = userService.getAvailableAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping(value = "/budgets", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getBudgets() throws UserNotFoundException, NotAuthenticatedClient {
        List<Budget> budgets = userService.getAllUsersBudgets();
        return new ResponseEntity<>(budgets, HttpStatus.OK);
    }

    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getCategories() throws UserNotFoundException, NotAuthenticatedClient {
        List<Category> categories = userService.getAllUsersCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping(value = "/debts", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getDebts() throws UserNotFoundException, NotAuthenticatedClient {
        List<Debt> debts = userService.getAllUsersDebts();
        return new ResponseEntity<>(debts, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<User> add(@RequestBody User user) {
        if (!userService.persist(user)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping(value = "/basic-info", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<User> updateUser(@RequestBody User user) throws Exception {
        return new ResponseEntity<>(userService.updateUserBasic(user), HttpStatus.CREATED);
    }

    @PostMapping(value = "/email", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<User> updateEmail(@RequestParam String email) throws Exception {
        return new ResponseEntity<>(userService.updateEmail(email), HttpStatus.CREATED);
    }

    @PostMapping(value = "/username", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<User> updateUsername(@RequestParam String username) throws Exception {
        return new ResponseEntity<>(userService.updateUsername(username), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/delete-account")
    ResponseEntity<Void> remove() throws NotAuthenticatedClient {
        userService.remove();
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
                "REST /account... returned error: " + exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
