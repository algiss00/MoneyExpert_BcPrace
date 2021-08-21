package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

    public List<BankAccount> getAllUsersAccounts(int id) throws UserNotFoundException {
        User u = userDao.find(id);
        if (u == null) {
            throw new UserNotFoundException();
        }
        return u.getAvailableBankAccounts();
    }

    public BankAccount getById(int id) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountDao.find(id);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException(id);
        }
        return bankAccount;
    }

    public List<User> getAllOwners(int accId) throws BankAccountNotFoundException {
        BankAccount b = getById(accId);
        return b.getOwners();
    }

    public boolean persist(BankAccount bankAccount, int uid) throws UserNotFoundException {
        if (bankAccount == null)
            throw new NullPointerException("bankAccount cannot be Null.");
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

    public void addNewOwner(int userId, int accId) throws UserNotFoundException, BankAccountNotFoundException {
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

    public boolean validate(BankAccount bankAccount) {
        return !bankAccount.getName().trim().isEmpty() && !alreadyExists(bankAccount);
    }

    public boolean alreadyExists(BankAccount bankAccount) {
        return bankAccountDao.find(bankAccount.getId()) != null;
    }


    public BankAccount updateAccount(int id, BankAccount bankAccount) throws BankAccountNotFoundException {
        BankAccount b = getById(id);
        b.setName(bankAccount.getName());
        b.setCurrency(bankAccount.getCurrency());
        return bankAccountDao.update(b);
    }

    // TODO: 30.05.2021 Pridej Prava, kontrolu pro delete
    public void remove(int id) throws NotAuthenticatedClient, BankAccountNotFoundException {
        BankAccount bankAccount = getById(id);
        if (!isUserOwnerOfBankAccount(SecurityUtils.getCurrentUser(), bankAccount)) {
            throw new NotAuthenticatedClient();
        }
        bankAccount.getOwners().clear();
        bankAccountDao.remove(bankAccount);
    }

    public void removeOwner(int userId, int accId) throws UserNotFoundException, BankAccountNotFoundException {
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


    public void removeTransFromAccount(int transId, int accId) throws TransactionNotFoundException, BankAccountNotFoundException {
        BankAccount b = bankAccountDao.find(accId);
        if (b == null) {
            throw new BankAccountNotFoundException();
        }
        Transaction t = transactionDao.find(transId);
        if (t == null) {
            throw new TransactionNotFoundException();
        }

        b.getTransactions().remove(t);
        t.setBankAccount(null);
        bankAccountDao.update(b);
        transactionDao.update(t);
    }

    public void removeBudgetFromBankAcc(int budgetId, int accId) throws BankAccountNotFoundException, BudgetNotFoundException {
        Budget budget = budgetDao.find(budgetId);
        if (budget == null) {
            throw new BudgetNotFoundException();
        }
        BankAccount bankAccount = bankAccountDao.find(accId);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException();
        }
        bankAccount.getBudgets().remove(budget);
        budget.setBankAccount(null);
        budgetDao.update(budget);
        bankAccountDao.update(bankAccount);
    }

    public void removeDebt(int id, int accId) throws DebtNotFoundException, BankAccountNotFoundException {
        Debt debt = debtDao.find(id);
        if (debt == null) {
            throw new DebtNotFoundException();
        }
        BankAccount bankAccount = bankAccountDao.find(accId);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException();
        }
        bankAccount.getDebts().remove(debt);
        debt.setCreator(null);
        debt.setBankAccount(null);
        debtDao.update(debt);
        bankAccountDao.update(bankAccount);
    }

    public void removeAllTrans(int accId) throws BankAccountNotFoundException, TransactionNotFoundException {
        BankAccount b = getById(accId);
        for (Transaction t : b.getTransactions()) {
            removeTransFromAccount(t.getId(), b.getId());
        }
    }

    private boolean isUserOwnerOfBankAccount(User user, BankAccount bankAccount) {
        List<User> owners = bankAccount.getOwners();
        for (User owner : owners) {
            if (owner == user) {
                return true;
            }
        }
        return false;
    }

    private Transaction createStartTransaction(BankAccount bankAccount, User user) {
        Transaction startTransaction = new Transaction();

        //todo
        Category startCategory = new Category();
        startCategory.getCreators().add(user);
        startCategory.setName("Start transaction");
        categoryDao.persist(startCategory);
        user.getMyCategories().add(startCategory);
        userDao.update(user);

        startTransaction.setBankAccount(bankAccount);
        startTransaction.setCategory(startCategory);
        startTransaction.setJottings("Start transaction");
        startTransaction.setAmount(bankAccount.getBalance());
        startTransaction.setDate(new Date().toString());
        startTransaction.setTypeTransaction(TypeTransaction.Income);

        transactionDao.persist(startTransaction);
        return startTransaction;
    }
}
