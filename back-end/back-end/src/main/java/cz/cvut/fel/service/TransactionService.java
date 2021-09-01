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
public class TransactionService {
    private TransactionDao transactionDao;
    private BankAccountDao bankAccountDao;
    private CategoryDao categoryDao;
    private BudgetDao budgetDao;
    private UserDao userDao;

    @Autowired
    public TransactionService(TransactionDao transactionDao, BankAccountDao bankAccountDao,
                              UserDao userDao, CategoryDao categoryDao, BudgetDao budgetDao) {
        this.transactionDao = transactionDao;
        this.bankAccountDao = bankAccountDao;
        this.categoryDao = categoryDao;
        this.budgetDao = budgetDao;
        this.userDao = userDao;
    }

    public List<Transaction> getAll() {
        return transactionDao.findAll();
    }

    //todo get all trans from category which are in BankAcc
//    public List<Transaction> getAllTransFromCategoryFromBankAcc(int catId, int accountId) throws
//            BankAccountNotFoundException, CategoryNotFoundException {
//        if (bankAccountDao.find(accountId) == null) {
//            throw new BankAccountNotFoundException();
//        }
//        Category category = categoryDao.find(catId);
//        if (category == null) {
//            throw new CategoryNotFoundException(catId);
//        }
//        if (bankAccountDao.find(accountId) == null) {
//            throw new BankAccountNotFoundException();
//        }
//
//        return transactionDao.getAllTransFromCategory(cat, accountId);
//    }

    public Transaction getById(int id) throws TransactionNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        Transaction t = transactionDao.find(id);
        if (t == null) {
            throw new TransactionNotFoundException(id);
        }
        if (!isOwnerOfTransaction(t)) {
            throw new NotAuthenticatedClient();
        }
        return t;
    }

    public boolean persist(Transaction transaction, int accId, int categoryId) throws BankAccountNotFoundException, CategoryNotFoundException, NotAuthenticatedClient, UserNotFoundException {
        Objects.requireNonNull(transaction);
        if (!validate(transaction))
            return false;

        BankAccount b = bankAccountDao.find(accId);
        if (b == null) {
            throw new BankAccountNotFoundException();
        }
        Category category = categoryDao.find(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException();
        }

        if (!isUserOwnerOfBankAccount(b) || !isCreatorOfCategory(category)) {
            throw new NotAuthenticatedClient();
        }

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY HH:mm");

        transaction.setBankAccount(b);
        transaction.setCategory(category);
        transaction.setDate(format.format(new Date()));
        transactionDao.persist(transaction);

        b.getTransactions().add(transaction);
        bankAccountLogic(b, transaction);
        bankAccountDao.update(b);
        return true;
    }

    private void budgetLogic(BankAccount bankAccount, Transaction transaction) {
        Category transCategory = transaction.getCategory();
        Budget budgetForTransaction = null;
        double transAmount = transaction.getAmount();

        for (Budget budget : bankAccount.getBudgets()) {
            if (budget.getCategory() == transCategory) {
                budgetForTransaction = budget;
            }
        }

        if (budgetForTransaction == null) {
            return;
        }

        budgetForTransaction.setSumAmount(budgetForTransaction.getSumAmount() + transAmount);
        budgetDao.update(budgetForTransaction);

        if (budgetForTransaction.getSumAmount() >= budgetForTransaction.getAmount()) {
            //todo notificate
            System.out.println("trans amount is bigger or equal then budget amount NOTIFICATE");
        } else {
            double percentOfSumAmount = budgetForTransaction.getSumAmount() * 100 / budgetForTransaction.getAmount();
            if (percentOfSumAmount >= budgetForTransaction.getPercentNotif()) {
                //todo notifdicate
                System.out.println("PROCENT NOTIFICATE");
            }
        }
    }

    private boolean validate(Transaction t) {
        if (transactionDao.find(t.getId()) != null) {
            return false;
        }
        return !(t.getAmount() <= 0);
    }

    private void bankAccountLogic(BankAccount bankAccount, Transaction transaction) {
        if (transaction.getTypeTransaction() == TypeTransaction.Expense) {
            bankAccount.setBalance(bankAccount.getBalance() - transaction.getAmount());
            budgetLogic(bankAccount, transaction);
        }
        if (transaction.getTypeTransaction() == TypeTransaction.Income) {
            bankAccount.setBalance(bankAccount.getBalance() + transaction.getAmount());
        }
    }

    public Transaction transferTransaction(int fromAccId, int toAccId, int transactionId) throws TransactionNotFoundException, BankAccountNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        Transaction transaction = getById(transactionId);
        Transaction transferTransaction = new Transaction();

        BankAccount toBankAcc = bankAccountDao.find(toAccId);
        if (toBankAcc == null) {
            throw new BankAccountNotFoundException();
        }
        BankAccount fromBankAcc = bankAccountDao.find(fromAccId);
        if (fromBankAcc == null) {
            throw new BankAccountNotFoundException();
        }
        if (!isUserOwnerOfBankAccount(toBankAcc) || !isUserOwnerOfBankAccount(fromBankAcc)) {
            throw new NotAuthenticatedClient();
        }
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY HH:mm");

        transferTransaction.setBankAccount(toBankAcc);
        transferTransaction.setCategory(transaction.getCategory());
        transferTransaction.setJottings("Transfer transaction from " + fromBankAcc.getName());
        transferTransaction.setAmount(transaction.getAmount());
        transferTransaction.setDate(format.format(new Date()));
        transferTransaction.setTypeTransaction(TypeTransaction.Income);

        transactionDao.persist(transferTransaction);
        bankAccountLogic(toBankAcc, transferTransaction);

        transaction.setTypeTransaction(TypeTransaction.Expense);
        bankAccountLogic(fromBankAcc, transaction);

        toBankAcc.getTransactions().add(transferTransaction);
        bankAccountDao.update(toBankAcc);
        return transaction;
    }

    public Transaction update(int id, Transaction t) throws TransactionNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        Transaction transaction = getById(id);
        BankAccount transBankAcc = transaction.getBankAccount();


        transaction.setAmount(t.getAmount());
        //todo!!!
        //updatedTransactionLogic(transaction, t, transBankAcc);

        transaction.setTypeTransaction(t.getTypeTransaction());


        transaction.setDate(t.getDate());
        transaction.setJottings(t.getJottings());
        return transactionDao.update(transaction);
    }

    private void updatedTransactionLogic(Transaction oldTransaction, Transaction updatedTransaction, BankAccount transBankAcc) {
        double balance = transBankAcc.getBalance();
        if (oldTransaction.getAmount() != updatedTransaction.getAmount()
                && oldTransaction.getTypeTransaction() != updatedTransaction.getTypeTransaction()
                && oldTransaction.getTypeTransaction() == TypeTransaction.Expense) {
            transBankAcc.setBalance(balance + oldTransaction.getAmount() - updatedTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        } else if (oldTransaction.getAmount() != updatedTransaction.getAmount() && oldTransaction.getTypeTransaction() == TypeTransaction.Income) {
            transBankAcc.setBalance(balance - oldTransaction.getAmount() + updatedTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        }
    }

    private void updatedTypeTransactionLogic(Transaction oldTransaction, Transaction updatedTransaction, BankAccount transBankAcc) {
        double balance = transBankAcc.getBalance();
        if (oldTransaction.getTypeTransaction() != updatedTransaction.getTypeTransaction()
                && updatedTransaction.getTypeTransaction() == TypeTransaction.Expense) {
            transBankAcc.setBalance(balance - updatedTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        } else if (oldTransaction.getTypeTransaction() != updatedTransaction.getTypeTransaction()
                && updatedTransaction.getTypeTransaction() == TypeTransaction.Income) {
            transBankAcc.setBalance(balance + updatedTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        }
    }

    public void remove(int id) throws TransactionNotFoundException, NotAuthenticatedClient, UserNotFoundException {
        Transaction transaction = getById(id);
        transaction.setCategory(null);
        transaction.setBankAccount(null);
        transactionDao.remove(transaction);
    }

    public void removeFromCategory(int transId) throws TransactionNotFoundException, UserNotFoundException, NotAuthenticatedClient {
        Transaction t = getById(transId);
        t.setCategory(null);
        transactionDao.update(t);
    }

    private boolean isOwnerOfTransaction(Transaction t) throws UserNotFoundException, NotAuthenticatedClient {
        User user = isLogged();
        List<BankAccount> bankAccounts = user.getAvailableBankAccounts();
        for (BankAccount bankAccount : bankAccounts) {
            if (bankAccount.getId() == t.getBankAccount().getId()) {
                return true;
            }
        }
        return false;
    }

    private boolean isUserOwnerOfBankAccount(BankAccount bankAccount) throws UserNotFoundException, NotAuthenticatedClient {
        User user = isLogged();
        List<User> owners = bankAccount.getOwners();
        for (User owner : owners) {
            if (owner.getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }

    private boolean isCreatorOfCategory(Category category) throws UserNotFoundException, NotAuthenticatedClient {
        User user = isLogged();
        List<User> creators = category.getCreators();
        for (User creator : creators) {
            if (creator.getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }

    private User isLogged() throws NotAuthenticatedClient, UserNotFoundException {
        if (SecurityUtils.getCurrentUser() == null) {
            throw new NotAuthenticatedClient();
        }
        User user = userDao.find(SecurityUtils.getCurrentUser().getId());
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }
}
