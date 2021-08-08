package cz.cvut.fel.service;

import cz.cvut.fel.dao.BankAccountDao;
import cz.cvut.fel.dao.TransactionDao;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.CategoryEnum;
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

    @Autowired
    public TransactionService(TransactionDao transactionDao, BankAccountDao bankAccountDao) {
        this.transactionDao = transactionDao;
        this.bankAccountDao = bankAccountDao;
    }

    public List<Transaction> getAll() {
        return transactionDao.findAll();
    }

    public List<Transaction> getAllFromAccount(int accountId) throws BankAccountNotFoundException {
        if (bankAccountDao.find(accountId) == null) {
            throw new BankAccountNotFoundException();
        }
        return transactionDao.getAllFromAccount(accountId);
    }

    public List<Transaction> getAllTransFromCategoryFromBankAcc(CategoryEnum cat, int accountId) throws
            BankAccountNotFoundException {
        if (bankAccountDao.find(accountId) == null) {
            throw new BankAccountNotFoundException();
        }

        return transactionDao.getAllTransFromCategory(cat, accountId);
    }

    public Transaction getById(int id) throws TransactionNotFoundException {
        Transaction t = transactionDao.find(id);
        if (t == null) {
            throw new TransactionNotFoundException(id);
        }
        return t;
    }

    public boolean persist(Transaction transaction, int accId, CategoryEnum category) throws BankAccountNotFoundException, CategoryNotFoundException {
        if (transaction == null)
            throw new NullPointerException("transaction can not be Null.");
        if (!validateIncome(transaction))
            return false;

        BankAccount b = bankAccountDao.find(accId);
        if (b == null) {
            throw new BankAccountNotFoundException();
        }
        transaction.setBankAccount(b);
        transaction.setCategory(category);
        transactionDao.persist(transaction);

        b.getTransactions().add(transaction);
        bankAccountDao.update(b);
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

    public void removeFromCategory(int transId) throws TransactionNotFoundException {
        Transaction t = getById(transId);
        t.setCategory(null);
        transactionDao.update(t);
    }
}
