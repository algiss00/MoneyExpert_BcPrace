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
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private static final Logger log = Logger.getLogger(UserController.class.getName());

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getUserById(@PathVariable int id) throws UserNotFoundException {
        User u = userService.getByIdUser(id);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @GetMapping(value = "/username", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getUserByUsername(@RequestParam String username) throws UserNotFoundException {
        User u = userService.getByUsername(username);
        if (u == null) {
            throw new UserNotFoundException(username);
        }
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @GetMapping(value = "/current-user", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getCurrentUser() throws NotAuthenticatedClient {
        User user = userService.getAuthenticatedUser();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(value = "/available-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAvailableAccounts() throws NotAuthenticatedClient {
        List<BankAccount> accounts = userService.getAvailableBankAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping(value = "/all-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllAccounts() throws Exception {
        List<BankAccount> accounts = userService.getAllUsersBankAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping(value = "/created-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getCreatedBankAccounts() throws NotAuthenticatedClient {
        List<BankAccount> accounts = userService.getCreatedBankAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getCategories() throws Exception {
        List<Category> categories = userService.getAllUsersCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<User> add(@RequestBody User user) throws Exception {
        User u = userService.persist(user);
        if (u == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(u, HttpStatus.CREATED);
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

    @PostMapping(value = "/password", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<User> updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword) throws Exception {
        userService.updatePassword(oldPassword, newPassword);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/delete-account")
    ResponseEntity<Void> remove() throws Exception {
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
            NotValidDataException.class,
            Exception.class})
    void handleExceptions(HttpServletResponse response, Exception exception)
            throws IOException {
        log.info(() ->
                "REST /users... returned error: " + exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
