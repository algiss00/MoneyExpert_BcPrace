package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class BankAccountService {
    private BankAccountDao bankAccountDao;
    private UserDao userDao;
    private TransactionDao transactionDao;
    private BudgetDao budgetDao;
    private DebtDao debtDao;
    private CategoryDao categoryDao;

    @Autowired
    public BankAccountService(CategoryDao categoryDao, BankAccountDao bankAccountDao, UserDao userDao,
                              TransactionDao transactionDao, BudgetDao budgetDao, DebtDao debtDao) {
        this.bankAccountDao = bankAccountDao;
        this.userDao = userDao;
        this.transactionDao = transactionDao;
        this.budgetDao = budgetDao;
        this.debtDao = debtDao;
        this.categoryDao = categoryDao;
    }

    public List<BankAccount> getAll() {
        return bankAccountDao.findAll();
    }

    public BankAccount getById(int id) throws BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        BankAccount bankAccount = bankAccountDao.find(id);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException(id);
        }
        if (!isUserOwnerOfBankAccount(bankAccount)) {
            throw new NotAuthenticatedClient();
        }
        return bankAccount;
    }

    public List<Budget> getAllAccountsBudgets(int accId) throws BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        BankAccount b = getById(accId);
        return b.getBudgets();
    }

    public List<Transaction> getAllTransactions(int accountId) throws BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        BankAccount b = getById(accountId);
        return b.getTransactions();
    }

    public List<Debt> getAllAccountsDebts(int accId) throws BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        BankAccount b = getById(accId);
        return b.getDebts();
    }

    public List<User> getAllOwners(int accId) throws BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        BankAccount b = getById(accId);
        return b.getOwners();
    }

    public boolean persist(BankAccount bankAccount, int uid) throws UserNotFoundException {
        Objects.requireNonNull(bankAccount);
        if (!validate(bankAccount))
            return false;
        User u = userDao.find(uid);
        if (u == null) {
            throw new UserNotFoundException();
        }
        bankAccount.getOwners().add(u);
        bankAccountDao.persist(bankAccount);
        u.getAvailableBankAccounts().add(bankAccount);
        userDao.update(u);
        createStartTransaction(bankAccount, u);
        return true;
    }

    public void addNewOwner(int userId, int accId) throws UserNotFoundException, BankAccountNotFoundException, NotAuthenticatedClient {
        User u = userDao.find(userId);
        if (u == null) {
            throw new UserNotFoundException();
        }
        BankAccount b = getById(accId);

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


    public BankAccount updateAccount(int id, BankAccount bankAccount) throws BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        BankAccount b = getById(id);
        b.setName(bankAccount.getName());
        b.setCurrency(bankAccount.getCurrency());
        return bankAccountDao.update(b);
    }

    public void remove(int id) throws NotAuthenticatedClient, BankAccountNotFoundException, UserNotFoundException {
        BankAccount bankAccount = getById(id);
        bankAccount.getOwners().clear();
        bankAccountDao.remove(bankAccount);
    }

    public void removeOwner(int userId, int accId) throws UserNotFoundException, BankAccountNotFoundException, NotAuthenticatedClient {
        User u = userDao.find(userId);
        if (u == null) {
            throw new UserNotFoundException();
        }
        BankAccount b = getById(accId);

        b.getOwners().remove(u);
        u.getAvailableBankAccounts().remove(b);
        bankAccountDao.update(b);
        userDao.update(u);
    }


    public void removeTransFromAccount(int transId, int accId) throws TransactionNotFoundException, BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        BankAccount b = getById(accId);
        Transaction t = transactionDao.find(transId);
        if (t == null) {
            throw new TransactionNotFoundException();
        }
        //todo check if this transaction in BAcc
        b.getTransactions().remove(t);
        t.setBankAccount(null);
        bankAccountDao.update(b);
        transactionDao.update(t);
    }

    public void removeBudgetFromBankAcc(int budgetId, int accId) throws BankAccountNotFoundException, BudgetNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        Budget budget = budgetDao.find(budgetId);
        if (budget == null) {
            throw new BudgetNotFoundException();
        }
        //todo check if budget exist in this BAcc
        BankAccount bankAccount = getById(accId);
        bankAccount.getBudgets().remove(budget);
        budget.setBankAccount(null);
        budgetDao.update(budget);
        bankAccountDao.update(bankAccount);
    }

    public void removeDebt(int id, int accId) throws DebtNotFoundException, BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        Debt debt = debtDao.find(id);
        if (debt == null) {
            throw new DebtNotFoundException();
        }
        BankAccount bankAccount = getById(accId);
        //todo check if debt exist in this BAcc
        bankAccount.getDebts().remove(debt);
        debt.setCreator(null);
        debt.setBankAccount(null);
        debtDao.update(debt);
        bankAccountDao.update(bankAccount);
    }

    public void removeAllTrans(int accId) throws BankAccountNotFoundException, TransactionNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        BankAccount b = getById(accId);
        for (Transaction t : b.getTransactions()) {
            removeTransFromAccount(t.getId(), b.getId());
        }
    }

    private boolean isUserOwnerOfBankAccount(BankAccount bankAccount) throws UserNotFoundException {
        User user = userDao.find(SecurityUtils.getCurrentUser().getId());
        if (user == null) {
            throw new UserNotFoundException();
        }
        List<User> owners = bankAccount.getOwners();
        for (User owner : owners) {
            if (owner.getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }

    private void createStartTransaction(BankAccount bankAccount, User user) {
        Transaction startTransaction = new Transaction();

        //todo default start category with Jakh
        Category startCategory = new Category();
        startCategory.getCreators().add(user);
        startCategory.setName("Start transaction");
        categoryDao.persist(startCategory);
        user.getMyCategories().add(startCategory);
        userDao.update(user);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY HH:mm");
        startTransaction.setBankAccount(bankAccount);
        startTransaction.setCategory(startCategory);
        startTransaction.setJottings("Start transaction");
        startTransaction.setAmount(bankAccount.getBalance());
        startTransaction.setDate(format.format(new Date()));
        startTransaction.setTypeTransaction(TypeTransaction.Income);

        transactionDao.persist(startTransaction);
    }
}
