package cz.cvut.fel.service.exceptions;

public class TransactionNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public TransactionNotFoundException(int id) {
        super("No Transaction with id = " + id);
    }

    public TransactionNotFoundException() {
        super("Not found Transaction");
    }
}
