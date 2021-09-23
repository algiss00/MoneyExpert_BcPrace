package cz.cvut.fel.service.exceptions;

public class BudgetNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public BudgetNotFoundException(String id) {
        super("No Budget with id = " + id);
    }

    public BudgetNotFoundException() {
        super("Not found Budget");
    }
}
