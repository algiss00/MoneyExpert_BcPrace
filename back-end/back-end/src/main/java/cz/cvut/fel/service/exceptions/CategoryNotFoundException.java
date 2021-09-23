package cz.cvut.fel.service.exceptions;

public class CategoryNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public CategoryNotFoundException(String id) {
        super("No Category with id = " + id);
    }

    public CategoryNotFoundException() {
        super("Not found Category");
    }
}
