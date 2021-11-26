package cz.cvut.fel.service.exceptions;

public class DebtNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public DebtNotFoundException(int id) {
        super("No Debt with id = " + id);
    }

    public DebtNotFoundException(String name) {
        super("No Debt with name = " + name);
    }

    public DebtNotFoundException() {
        super("Not found Debt");
    }
}
