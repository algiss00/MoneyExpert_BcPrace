package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
import cz.cvut.fel.service.exceptions.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class BankAccountService extends AbstractServiceHelper {
    public BankAccountService(UserDao userDao, BankAccountDao bankAccountDao, TransactionDao transactionDao,
                              BudgetDao budgetDao, DebtDao debtDao, CategoryDao categoryDao,
                              NotifyBudgetDao notifyBudgetDao, NotifyDebtDao notifyDebtDao) {
        super(userDao, bankAccountDao, transactionDao, budgetDao, debtDao, categoryDao, notifyBudgetDao, notifyDebtDao);
    }

    public List<BankAccount> getAll() {
        return bankAccountDao.findAll();
    }

    public List<Budget> getAllAccountsBudgets(int accId) throws BankAccountNotFoundException, NotAuthenticatedClient {
        BankAccount b = getByIdBankAccount(accId);
        return b.getBudgets();
    }

    public List<Transaction> getAllTransactions(int accountId) throws BankAccountNotFoundException, NotAuthenticatedClient {
        BankAccount b = getByIdBankAccount(accountId);
        return b.getTransactions();
    }

    public List<Debt> getAllAccountsDebts(int accId) throws BankAccountNotFoundException, NotAuthenticatedClient {
        BankAccount b = getByIdBankAccount(accId);
        return b.getDebts();
    }

    public List<User> getAllOwners(int accId) throws BankAccountNotFoundException, NotAuthenticatedClient {
        BankAccount b = getByIdBankAccount(accId);
        return b.getOwners();
    }

    public boolean persist(BankAccount bankAccount) throws NotAuthenticatedClient, CategoryNotFoundException {
        Objects.requireNonNull(bankAccount);
        if (!validate(bankAccount))
            return false;
        User u = isLogged();
        bankAccount.getOwners().add(u);
        bankAccountDao.persist(bankAccount);
        u.getAvailableBankAccounts().add(bankAccount);
        userDao.update(u);
        if (bankAccount.getBalance() != 0) {
            createStartTransaction(bankAccount, u);
        }
        return true;
    }

    public void addNewOwner(int userId, int accId) throws UserNotFoundException, BankAccountNotFoundException, NotAuthenticatedClient {
        BankAccount b = getByIdBankAccount(accId);
        User u = getByIdUser(userId);

        b.getOwners().add(u);
        u.getAvailableBankAccounts().add(b);
        bankAccountDao.update(b);
        userDao.update(u);
    }

    private boolean validate(BankAccount bankAccount) {
        return !bankAccount.getName().trim().isEmpty() && !alreadyExists(bankAccount);
    }

    private boolean alreadyExists(BankAccount bankAccount) {
        return bankAccountDao.find(bankAccount.getId()) != null;
    }


    public BankAccount updateAccount(int id, BankAccount bankAccount) throws BankAccountNotFoundException, NotAuthenticatedClient {
        BankAccount b = getByIdBankAccount(id);
        b.setName(bankAccount.getName());
        b.setCurrency(bankAccount.getCurrency());
        return bankAccountDao.update(b);
    }

    public void remove(int id) throws NotAuthenticatedClient, BankAccountNotFoundException {
        BankAccount bankAccount = getByIdBankAccount(id);
        bankAccount.getOwners().clear();
        bankAccountDao.remove(bankAccount);
    }

    public void removeOwner(int userId, int accId) throws UserNotFoundException, BankAccountNotFoundException, NotAuthenticatedClient {
        BankAccount b = getByIdBankAccount(accId);
        User u = getByIdUser(userId);

        b.getOwners().remove(u);
        u.getAvailableBankAccounts().remove(b);
        bankAccountDao.update(b);
        userDao.update(u);
    }

    public void removeTransFromAccount(int transId, int accId) throws TransactionNotFoundException, BankAccountNotFoundException, NotAuthenticatedClient {
        BankAccount b = getByIdBankAccount(accId);
        Transaction t = getByIdTransaction(transId);
        if (!isTransactionInBankAcc(b.getId(), t.getId())) {
            throw new NotAuthenticatedClient();
        }
        b.getTransactions().remove(t);
        t.setBankAccount(null);
        bankAccountDao.update(b);
        transactionDao.update(t);
    }

    private boolean isTransactionInBankAcc(int bankAccountId, int transactionId) {
        return transactionDao.getFromBankAcc(bankAccountId, transactionId) != null;
    }

    public void removeBudgetFromBankAcc(int budgetId, int accId) throws BankAccountNotFoundException, BudgetNotFoundException, NotAuthenticatedClient {
        Budget budget = budgetDao.find(budgetId);
        if (budget == null) {
            throw new BudgetNotFoundException();
        }
        BankAccount bankAccount = getByIdBankAccount(accId);
        if (!isBudgetInBankAcc(bankAccount, budget)) {
            throw new NotAuthenticatedClient();
        }
        bankAccount.getBudgets().remove(budget);
        budget.setBankAccount(null);
        budgetDao.update(budget);
        bankAccountDao.update(bankAccount);
    }

    private boolean isBudgetInBankAcc(BankAccount bankAccount, Budget budget) {
        List<Budget> budgets = bankAccount.getBudgets();
        for (Budget b : budgets) {
            if (b.getId() == budget.getId()) {
                return true;
            }
        }
        return false;
    }

    private boolean isDebtInBankAcc(BankAccount bankAccount, Debt d) {
        List<Debt> debts = bankAccount.getDebts();
        for (Debt debt : debts) {
            if (debt.getId() == d.getId()) {
                return true;
            }
        }
        return false;
    }

    public void removeDebt(int id, int accId) throws DebtNotFoundException, BankAccountNotFoundException, NotAuthenticatedClient {
        Debt debt = debtDao.find(id);
        if (debt == null) {
            throw new DebtNotFoundException();
        }
        BankAccount bankAccount = getByIdBankAccount(accId);
        if (!isDebtInBankAcc(bankAccount, debt)) {
            throw new NotAuthenticatedClient();
        }
        bankAccount.getDebts().remove(debt);
        debt.setCreator(null);
        debt.setBankAccount(null);
        debtDao.update(debt);
        bankAccountDao.update(bankAccount);
    }

    public void removeAllTrans(int accId) throws BankAccountNotFoundException, TransactionNotFoundException, NotAuthenticatedClient {
        BankAccount b = getByIdBankAccount(accId);
        for (Transaction t : b.getTransactions()) {
            removeTransFromAccount(t.getId(), b.getId());
        }
    }


    private void createStartTransaction(BankAccount bankAccount, User user) throws CategoryNotFoundException, NotAuthenticatedClient {
        Transaction startTransaction = new Transaction();

        //todo default start category with Jakh
//        Category startCategory = new Category();
//        startCategory.getCreators().add(user);
//        startCategory.setName("Start transaction");
//        categoryDao.persist(startCategory);
//        user.getMyCategories().add(startCategory);
//        userDao.update(user);
        // todo myslis v pohode?
        Category category = getByIdCategory(6);

        startTransaction.setBankAccount(bankAccount);
        startTransaction.setCategory(category);
        startTransaction.setJottings("Start transaction");
        startTransaction.setAmount(bankAccount.getBalance());
        startTransaction.setDate(new Date());
        startTransaction.setTypeTransaction(TypeTransaction.INCOME);

        transactionDao.persist(startTransaction);
    }
}
