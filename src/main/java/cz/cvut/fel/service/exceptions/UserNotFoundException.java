package cz.cvut.fel.service.exceptions;

public class UserNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(int id) {
        super("No User with id = " + id);
    }

    public UserNotFoundException(String username) {
        super("No User with username = " + username);
    }

    public UserNotFoundException() {
        super("Not found User");
    }
}
