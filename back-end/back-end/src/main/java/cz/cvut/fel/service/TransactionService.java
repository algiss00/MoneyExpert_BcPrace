package cz.cvut.fel.service;

import cz.cvut.fel.dao.BankAccountDao;
import cz.cvut.fel.dao.CategoryDao;
import cz.cvut.fel.dao.TransactionDao;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Category;
import cz.cvut.fel.model.Transaction;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.BankAccountNotFoundException;
import cz.cvut.fel.service.exceptions.CategoryNotFoundException;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.TransactionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TransactionService {
    private TransactionDao transactionDao;
    private BankAccountDao bankAccountDao;
    private BankAccountService bankAccountService;
    private CategoryDao categoryDao;

    @Autowired
    public TransactionService(TransactionDao transactionDao, BankAccountDao bankAccountDao, CategoryDao categoryDao,
                              BankAccountService bankAccountService) {
        this.transactionDao = transactionDao;
        this.bankAccountDao = bankAccountDao;
        this.categoryDao = categoryDao;
        this.bankAccountService = bankAccountService;
    }

    public List<Transaction> getAll() {
        return transactionDao.findAll();
    }

    public List<Transaction> getAllFromAccount(int accountId) throws BankAccountNotFoundException {
        bankAccountService.getById(accountId);
        return transactionDao.getAllFromAccount(accountId);
    }

    public List<Transaction> getAllTransFromCategoryFromBankAcc(int catId, int accountId) throws
            BankAccountNotFoundException, CategoryNotFoundException {
        bankAccountService.getById(accountId);
        if (categoryDao.find(catId) == null) {
            throw new CategoryNotFoundException(catId);
        }

        return transactionDao.getAllTransFromCategory(catId, accountId);
    }

    public Transaction getById(int id) throws TransactionNotFoundException {
        Transaction t = transactionDao.find(id);
        if (t == null) {
            throw new TransactionNotFoundException(id);
        }
        return t;
    }

    public boolean persist(Transaction transaction, int accId, int categoryId) throws BankAccountNotFoundException, CategoryNotFoundException {
        if (transaction == null)
            throw new NullPointerException("transaction can not be Null.");
        if (!validateIncome(transaction))
            return false;

        BankAccount b = bankAccountService.getById(accId);
        Category category = categoryDao.find(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException(categoryId);
        }
        transaction.setBankAccount(b);
        transaction.setCategory(category);
        transactionDao.persist(transaction);

        b.getTransactions().add(transaction);
        bankAccountDao.update(b);
        category.getTransactions().add(transaction);
        categoryDao.update(category);
        return true;
    }

    public boolean validateIncome(Transaction t) {
        return !(t.getAmount() <= 0);
    }

    public Transaction update(int id, Transaction t) throws TransactionNotFoundException {
        Transaction transaction = getById(id);

        transaction.setAmount(t.getAmount());
        transaction.setDate(t.getDate());
        transaction.setJottings(t.getJottings());
        return transactionDao.update(transaction);
    }

    public void remove(int id) throws TransactionNotFoundException, NotAuthenticatedClient {
        if (SecurityUtils.getCurrentUser() == null) {
            throw new NotAuthenticatedClient();
        }
        Transaction transaction = getById(id);
        transaction.setCategory(null);
        transaction.setBankAccount(null);
        transactionDao.remove(transaction);
    }

    public void removeFromAccount(int transId, int accId) throws TransactionNotFoundException, BankAccountNotFoundException {
        BankAccount b = bankAccountService.getById(accId);
        Transaction t = getById(transId);

        b.getTransactions().remove(t);
        t.setBankAccount(null);
        bankAccountDao.update(b);
        transactionDao.update(t);
    }

    public void removeFromCategory(int transId, int catId) throws CategoryNotFoundException, TransactionNotFoundException {
        Category category = categoryDao.find(catId);
        if (category == null) {
            throw new CategoryNotFoundException(catId);
        }
        Transaction t = getById(transId);

        category.getTransactions().remove(t);
        t.setCategory(null);
        categoryDao.update(category);
        transactionDao.update(t);
    }
}
