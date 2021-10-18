package generator;

import cz.cvut.fel.dto.TypeCurrency;
import cz.cvut.fel.dto.TypeNotification;
import cz.cvut.fel.dto.TypeTransaction;
import cz.cvut.fel.model.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Generator {
    private static final Random RAND = new Random();

    public static User generateUser(String username, String password, String name, String lastname, String email) {
        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setLastname(lastname);
        user.setEmail(email);
        return user;
    }

    public static User generateDefaultUser() {
        final User user = new User();
        user.setUsername("username" + randomInt());
        user.setPassword("password");
        user.setName("name");
        user.setLastname("lastname");
        user.setEmail("email" + randomInt());
        return user;
    }

    public static Transaction generateDefaultTransaction() throws ParseException {
        final Transaction transaction = new Transaction();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse("2021-10-02");

        transaction.setTypeTransaction(TypeTransaction.EXPENSE);
        transaction.setAmount(1000);
        transaction.setJottings("jottings test");
        transaction.setDate(date);
        return transaction;
    }

    public static BankAccount generateDefaultBankAccount() {
        final BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(1000);
        bankAccount.setCurrency(TypeCurrency.CZK);
        bankAccount.setName("Test ucet" + randomInt());
        return bankAccount;
    }

    public static Category generateDefaultCategory() {
        final Category category = new Category();
        category.setName("Test category" + randomInt());
        return category;
    }

    public static NotifyBudget generateDefaultBudgetNotify() {
        final NotifyBudget notifyBudget = new NotifyBudget();
        notifyBudget.setBudget(generateDefaultBudget());
        notifyBudget.setCreator(generateDefaultUser());
        notifyBudget.setTypeNotification(TypeNotification.BUDGET_PERCENT);
        return notifyBudget;
    }

    public static NotifyDebt generateDefaultDebtNotify() throws ParseException {
        final NotifyDebt notifyDebt = new NotifyDebt();
        notifyDebt.setDebt(generateDefaultDebt());
        notifyDebt.setCreator(generateDefaultUser());
        notifyDebt.setTypeNotification(TypeNotification.DEBT_DEADLINE);
        return notifyDebt;
    }

    public static Debt generateDefaultDebt() throws ParseException {
        final Debt debt = new Debt();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = formatter.parse("2021-10-20");
        Date notify = formatter.parse("2021-10-19");
        debt.setName("Test debt" + randomInt());
        debt.setDescription("test description");
        debt.setAmount(1000);
        debt.setDeadline(deadline);
        debt.setNotifyDate(notify);
        return debt;
    }

    public static Budget generateDefaultBudget() {
        final Budget budget = new Budget();
        budget.setPercentNotif(50);
        budget.setAmount(1000);
        budget.setName("Test budget" + randomInt());
        return budget;
    }

    public static List<Category> generateDefaultCategories() {
        List<Category> categories = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            Category category = new Category();
            category.setName("DefaultCategory" + i);
            categories.add(category);
        }
        return categories;
    }

    public static int randomInt() {
        return RAND.nextInt();
    }
}
