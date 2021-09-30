package cz.cvut.fel.service.exceptions;

public class NotValidDataException extends Exception {
    private static final long serialVersionUID = 1L;

    public NotValidDataException(String entityName) {
        super("Not valid data for entity " + entityName);
    }

    public NotValidDataException() {
        super("Not valid data");
    }
}
