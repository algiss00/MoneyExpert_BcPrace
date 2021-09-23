package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.model.*;
import cz.cvut.fel.service.exceptions.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class TransactionService extends AbstractServiceHelper {

    public TransactionService(UserDao userDao, BankAccountDao bankAccountDao, TransactionDao transactionDao,
                              BudgetDao budgetDao, DebtDao debtDao, CategoryDao categoryDao,
                              NotifyBudgetDao notifyBudgetDao, NotifyDebtDao notifyDebtDao) {
        super(userDao, bankAccountDao, transactionDao, budgetDao, debtDao, categoryDao, notifyBudgetDao, notifyDebtDao);
    }

    public List<Transaction> getAll() {
        return transactionDao.findAll();
    }

    public List<Transaction> getAllTransFromCategoryFromBankAcc(String catId, String accountId) throws
            BankAccountNotFoundException, CategoryNotFoundException, NotAuthenticatedClient {
        getByIdBankAccount(accountId);
        getByIdCategory(catId);
        return transactionDao.getAllTransFromCategory(catId, accountId);
    }

    public boolean persist(Transaction transaction, String accId, String categoryId) throws BankAccountNotFoundException, CategoryNotFoundException, NotAuthenticatedClient {
        Objects.requireNonNull(transaction);
        if (!validate(transaction))
            return false;

        BankAccount b = getByIdBankAccount(accId);
        Category category = getByIdCategory(categoryId);

        //SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        transaction.setBankAccount(b);
        transaction.setCategory(category);
        transaction.setDate(new Date());
        transactionDao.persist(transaction);

        b.getTransactions().add(transaction);
        bankAccountLogic(b, transaction);
        bankAccountDao.update(b);
        return true;
    }

    private void budgetLogic(BankAccount bankAccount, Transaction transaction) {
        Category transCategory = transaction.getCategory();
        double transAmount = transaction.getAmount();

        Budget budgetForTransaction = budgetDao.getByCategory(transCategory.getId(), bankAccount.getId());
        if (budgetForTransaction == null) {
            System.out.println("NO BUDGET FOR THIS CATEGORY");
            return;
        }

        budgetForTransaction.setSumAmount(budgetForTransaction.getSumAmount() + transAmount);
        budgetDao.update(budgetForTransaction);
        double percentOfSumAmount = budgetForTransaction.getSumAmount() * 100 / budgetForTransaction.getAmount();

        if (budgetForTransaction.getSumAmount() >= budgetForTransaction.getAmount()) {
            //todo notificate
            System.out.println("trans amount is bigger or equal then budget amount NOTIFICATE");
            if (notifyBudgetDao.alreadyExistsBudget(budgetForTransaction.getId(), TypeNotification.BUDGET_AMOUNT)) {
                System.out.println("EXISTS");
                return;
            }
            NotifyBudget notifyBudgetEntity = new NotifyBudget();
            notifyBudgetEntity.setCreator(budgetForTransaction.getCreator());
            notifyBudgetEntity.setBudget(budgetForTransaction);
            notifyBudgetEntity.setTypeNotification(TypeNotification.BUDGET_AMOUNT);

            notifyBudgetDao.persist(notifyBudgetEntity);
            System.out.println("ADDED TO BUDGET AMOUNT " + notifyBudgetEntity.getBudget().getName());
        } else if (percentOfSumAmount >= budgetForTransaction.getPercentNotif()) {
            //todo notifdicate
            System.out.println("PROCENT NOTIFICATE");
            if (notifyBudgetDao.alreadyExistsBudget(budgetForTransaction.getId(), TypeNotification.BUDGET_PERCENT)) {
                System.out.println("EXISTS");
                return;
            }
            NotifyBudget notifyBudgetEntity = new NotifyBudget();
            notifyBudgetEntity.setCreator(budgetForTransaction.getCreator());
            notifyBudgetEntity.setBudget(budgetForTransaction);
            notifyBudgetEntity.setTypeNotification(TypeNotification.BUDGET_PERCENT);

            notifyBudgetDao.persist(notifyBudgetEntity);
            System.out.println("ADDED TO BUDGET PERCENT " + notifyBudgetEntity.getBudget().getName());
        }
    }

    private boolean validate(Transaction t) {
        if (transactionDao.find(t.getId()) != null) {
            return false;
        }
        return !(t.getAmount() <= 0);
    }

    private void bankAccountLogic(BankAccount bankAccount, Transaction transaction) {
        if (transaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            bankAccount.setBalance(bankAccount.getBalance() - transaction.getAmount());
            budgetLogic(bankAccount, transaction);
        }
        if (transaction.getTypeTransaction() == TypeTransaction.INCOME) {
            bankAccount.setBalance(bankAccount.getBalance() + transaction.getAmount());
        }
    }

    public Transaction transferTransaction(String fromAccId, String toAccId, String transactionId) throws TransactionNotFoundException, BankAccountNotFoundException, NotAuthenticatedClient {
        Transaction transaction = getByIdTransaction(transactionId);
        Transaction transferTransaction = new Transaction();

        BankAccount toBankAcc = getByIdBankAccount(toAccId);
        BankAccount fromBankAcc = getByIdBankAccount(fromAccId);

        //SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        transferTransaction.setBankAccount(toBankAcc);
        transferTransaction.setCategory(transaction.getCategory());
        if (transaction.getJottings().isEmpty()) {
            transferTransaction.setJottings("Transfer transaction from " + fromBankAcc.getName());
        } else {
            transferTransaction.setJottings(transaction.getJottings());
        }
        transferTransaction.setAmount(transaction.getAmount());
        transferTransaction.setDate(new Date());
        transferTransaction.setTypeTransaction(TypeTransaction.INCOME);

        transactionDao.persist(transferTransaction);
        bankAccountLogic(toBankAcc, transferTransaction);

        transaction.setTypeTransaction(TypeTransaction.EXPENSE);
        bankAccountLogic(fromBankAcc, transaction);

        toBankAcc.getTransactions().add(transferTransaction);
        bankAccountDao.update(toBankAcc);
        return transaction;
    }

    public Transaction update(String id, Transaction t) throws TransactionNotFoundException, NotAuthenticatedClient {
        Transaction transaction = getByIdTransaction(id);
        BankAccount transBankAcc = transaction.getBankAccount();

        updatedTransactionLogic(transaction, t, transBankAcc);

        transaction.setAmount(t.getAmount());
        transaction.setDate(t.getDate());
        transaction.setJottings(t.getJottings());
        return transactionDao.update(transaction);
    }

    public Transaction updateTransactionType(String id, TypeTransaction typeTransaction) throws TransactionNotFoundException, NotAuthenticatedClient {
        Transaction transaction = getByIdTransaction(id);
        BankAccount transBankAcc = transaction.getBankAccount();

        updateTransactionTypeLogic(transaction, typeTransaction, transBankAcc);

        transaction.setTypeTransaction(typeTransaction);
        return transactionDao.update(transaction);
    }

    private void updateTransactionTypeLogic(Transaction oldTransaction, TypeTransaction typeTransaction, BankAccount transBankAcc) {
        double balance = transBankAcc.getBalance();
        if (oldTransaction.getTypeTransaction() != typeTransaction && typeTransaction == TypeTransaction.EXPENSE) {
            transBankAcc.setBalance(balance - oldTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        } else if (oldTransaction.getTypeTransaction() != typeTransaction && typeTransaction == TypeTransaction.INCOME) {
            transBankAcc.setBalance(balance + oldTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        }
    }

    private void updatedTransactionLogic(Transaction oldTransaction, Transaction updatedTransaction, BankAccount transBankAcc) {
        double balance = transBankAcc.getBalance();
        if (oldTransaction.getAmount() != updatedTransaction.getAmount() && oldTransaction.getTypeTransaction() == TypeTransaction.EXPENSE) {
            transBankAcc.setBalance(balance + oldTransaction.getAmount() - updatedTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        } else if (oldTransaction.getAmount() != updatedTransaction.getAmount() && oldTransaction.getTypeTransaction() == TypeTransaction.INCOME) {
            transBankAcc.setBalance(balance - oldTransaction.getAmount() + updatedTransaction.getAmount());
            bankAccountDao.update(transBankAcc);
        }
    }

    public void remove(String id) throws TransactionNotFoundException, NotAuthenticatedClient {
        Transaction transaction = getByIdTransaction(id);
        transaction.setCategory(null);
        transaction.setBankAccount(null);
        transactionDao.remove(transaction);
    }

    public void removeFromCategory(String transId) throws TransactionNotFoundException, NotAuthenticatedClient {
        Transaction t = getByIdTransaction(transId);
        t.setCategory(null);
        transactionDao.update(t);
    }
}
