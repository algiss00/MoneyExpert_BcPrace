package cz.cvut.fel.service.exceptions;

public class NotAuthenticatedClient extends Exception {
    private static final long serialVersionUID = 1L;

    public NotAuthenticatedClient() {
        super("Not authenticated client");
    }
}
