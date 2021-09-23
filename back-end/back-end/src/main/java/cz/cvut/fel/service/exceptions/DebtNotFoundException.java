package cz.cvut.fel.service.exceptions;

public class DebtNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public DebtNotFoundException(String id) {
        super("No Debt with id = " + id);
    }

    public DebtNotFoundException() {
        super("Not found Debt");
    }
}
