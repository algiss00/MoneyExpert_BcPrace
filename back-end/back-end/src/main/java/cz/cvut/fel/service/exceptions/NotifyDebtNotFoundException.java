package cz.cvut.fel.service.exceptions;

public class NotifyDebtNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public NotifyDebtNotFoundException() {
        super("Not found notifyDebt");
    }
}
