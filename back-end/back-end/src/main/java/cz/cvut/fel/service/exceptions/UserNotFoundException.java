package cz.cvut.fel.service.exceptions;

public class UserNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String id) {
        super("No User with id = " + id);
    }

    public UserNotFoundException() {
        super("Not found User");
    }
}
