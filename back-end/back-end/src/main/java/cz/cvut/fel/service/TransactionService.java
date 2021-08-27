package cz.cvut.fel.service;

import cz.cvut.fel.dao.BankAccountDao;
import cz.cvut.fel.dao.BudgetDao;
import cz.cvut.fel.dao.CategoryDao;
import cz.cvut.fel.dao.TransactionDao;
import cz.cvut.fel.model.*;
import cz.cvut.fel.security.SecurityUtils;
import cz.cvut.fel.service.exceptions.BankAccountNotFoundException;
import cz.cvut.fel.service.exceptions.CategoryNotFoundException;
import cz.cvut.fel.service.exceptions.NotAuthenticatedClient;
import cz.cvut.fel.service.exceptions.TransactionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    public TransactionService(TransactionDao transactionDao, BankAccountDao bankAccountDao, CategoryDao categoryDao, BudgetDao budgetDao) {
        this.transactionDao = transactionDao;
        this.bankAccountDao = bankAccountDao;
        this.categoryDao = categoryDao;
        this.budgetDao = budgetDao;
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

    public Transaction getById(int id) throws TransactionNotFoundException {
        Transaction t = transactionDao.find(id);
        if (t == null) {
            throw new TransactionNotFoundException(id);
        }
        return t;
    }

    public boolean persist(Transaction transaction, int accId, int categoryId) throws BankAccountNotFoundException, CategoryNotFoundException {
        Objects.requireNonNull(transaction);
        if (!validateAmount(transaction))
            return false;

        BankAccount b = bankAccountDao.find(accId);
        if (b == null) {
            throw new BankAccountNotFoundException();
        }
        Category category = categoryDao.find(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException();
        }
        transaction.setBankAccount(b);
        transaction.setCategory(category);
        transaction.setDate(new Date().toString());
        transactionDao.persist(transaction);

        b.getTransactions().add(transaction);
        bankAccountLogic(b, transaction);
        bankAccountDao.update(b);
        budgetLogic(b, transaction);
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
            System.out.println("trans amount is bigger then budget amount NOTIFICATE");
        } else {
            double percentOfSumAmount = budgetForTransaction.getSumAmount() * 100 / budgetForTransaction.getAmount();
            if (percentOfSumAmount >= budgetForTransaction.getPercentNotif()) {
                //todo notifdicate
                System.out.println("PROCENT NOTIFICATE");
            }
        }


    }

    public boolean validateAmount(Transaction t) {
        return !(t.getAmount() <= 0);
    }

    private void bankAccountLogic(BankAccount bankAccount, Transaction transaction) {
        if (transaction.getTypeTransaction() == TypeTransaction.Expense) {
            bankAccount.setBalance(bankAccount.getBalance() - transaction.getAmount());
        }
        if (transaction.getTypeTransaction() == TypeTransaction.Income) {
            bankAccount.setBalance(bankAccount.getBalance() + transaction.getAmount());
        }
    }

    public Transaction transferTransaction(int fromAccId, int toAccId, int transactionId) throws TransactionNotFoundException, BankAccountNotFoundException {
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

        transferTransaction.setBankAccount(toBankAcc);
        transferTransaction.setCategory(transaction.getCategory());
        transferTransaction.setJottings("Transfer transaction from " + fromBankAcc.getName());
        transferTransaction.setAmount(transaction.getAmount());
        transferTransaction.setDate(new Date().toString());
        transferTransaction.setTypeTransaction(TypeTransaction.Income);

        transactionDao.persist(transferTransaction);
        bankAccountLogic(toBankAcc, transferTransaction);

        transaction.setTypeTransaction(TypeTransaction.Expense);
        bankAccountLogic(fromBankAcc, transaction);

        toBankAcc.getTransactions().add(transferTransaction);
        bankAccountDao.update(toBankAcc);
        return transaction;
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
