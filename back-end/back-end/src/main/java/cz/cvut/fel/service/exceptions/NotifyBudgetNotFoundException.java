package cz.cvut.fel.service.exceptions;

public class NotifyBudgetNotFoundException extends Exception{
    private static final long serialVersionUID = 1L;

    public NotifyBudgetNotFoundException() {
        super("Not found notifyBudget");
    }
}
