package cz.cvut.fel.controller;

import cz.cvut.fel.model.*;
import cz.cvut.fel.service.CategoryService;
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
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    private static final Logger log = Logger.getLogger(CategoryController.class.getName());

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllCategory() {
        List<Category> cateAll = categoryService.getAll();
        return new ResponseEntity<>(cateAll, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getCategoryById(@PathVariable int id) throws Exception {
        Category c = categoryService.getByIdCategory(id);
        return new ResponseEntity<>(c, HttpStatus.OK);
    }

    @GetMapping(value = "/budget/{catId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getBudget(@PathVariable int catId) throws Exception {
        Budget budget = categoryService.getBudget(catId);
        return new ResponseEntity<>(budget, HttpStatus.OK);
    }

    @GetMapping(value = "/transactions/{catId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAllTransactions(@PathVariable int catId) throws Exception {
        List<Transaction> c = categoryService.getTransactions(catId);
        return new ResponseEntity<>(c, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Category> add(@RequestBody Category category) throws Exception {
        Category c = categoryService.persist(category);
        if (c == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(c, HttpStatus.CREATED);
    }

    @PostMapping(value = "/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> addTransactionToCategory(@RequestParam int transId, @RequestParam int categoryId) throws Exception {
        categoryService.addTransactionToCategory(transId, categoryId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Category> updateCat(@RequestParam int catId, @RequestBody Category category) throws Exception {
        return new ResponseEntity<>(categoryService.updateCategory(catId, category), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> remove(@PathVariable int id) throws Exception {
        categoryService.remove(id);
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
                "REST /category... returned error: " + exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}