package cz.cvut.fel.service.exceptions;

public class BankAccountNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public BankAccountNotFoundException(String id) {
        super("No account with id = " + id);
    }

    public BankAccountNotFoundException() {
        super("Not found BankAccount");
    }
}
