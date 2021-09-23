package cz.cvut.fel.service.exceptions;

public class CategoryNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public CategoryNotFoundException(int id) {
        super("No Category with id = " + id);
    }

    public CategoryNotFoundException(String name) {
        super("No Category with name = " + name);
    }

    public CategoryNotFoundException() {
        super("Not found Category");
    }
}
