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
public class TransactionService {
    private TransactionDao transactionDao;
    private BankAccountDao bankAccountDao;
    private CategoryDao categoryDao;
    private BudgetDao budgetDao;
    private UserDao userDao;
    private NotifyBudgetDao notifyBudgetDao;
    private UserService userService;
    private BankAccountService bankAccountService;
    private CategoryService categoryService;

    public TransactionService(TransactionDao transactionDao, BankAccountDao bankAccountDao,
                              UserDao userDao, CategoryDao categoryDao, BudgetDao budgetDao, NotifyBudgetDao notifyBudgetDao, DebtDao debtDao) {
        this.transactionDao = transactionDao;
        this.bankAccountDao = bankAccountDao;
        this.categoryDao = categoryDao;
        this.budgetDao = budgetDao;
        this.userDao = userDao;
        this.notifyBudgetDao = notifyBudgetDao;
        this.userService = new UserService(userDao);
        this.bankAccountService = new BankAccountService(categoryDao, bankAccountDao, userDao, transactionDao, budgetDao, debtDao, notifyBudgetDao);
        this.categoryService = new CategoryService(categoryDao, userDao, transactionDao, bankAccountDao, budgetDao, notifyBudgetDao, debtDao);
    }

    public List<Transaction> getAll() {
        return transactionDao.findAll();
    }

    public List<Transaction> getAllTransFromCategoryFromBankAcc(int catId, int accountId) throws
            BankAccountNotFoundException, CategoryNotFoundException, NotAuthenticatedClient {
        BankAccount bankAccount = bankAccountService.getById(accountId);
        Category category = categoryService.getById(catId);
        return transactionDao.getAllTransFromCategory(catId, accountId);
    }

    public Transaction getById(int id) throws TransactionNotFoundException, NotAuthenticatedClient {
        Transaction t = transactionDao.find(id);
        if (t == null) {
            throw new TransactionNotFoundException(id);
        }
        if (!isOwnerOfTransaction(t)) {
            throw new NotAuthenticatedClient();
        }
        return t;
    }

    public boolean persist(Transaction transaction, int accId, int categoryId) throws BankAccountNotFoundException, CategoryNotFoundException, NotAuthenticatedClient {
        Objects.requireNonNull(transaction);
        if (!validate(transaction))
            return false;

        BankAccount b = bankAccountService.getById(accId);
        Category category = categoryService.getById(categoryId);

        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

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

    public Transaction transferTransaction(int fromAccId, int toAccId, int transactionId) throws TransactionNotFoundException, BankAccountNotFoundException, NotAuthenticatedClient {
        Transaction transaction = getById(transactionId);
        Transaction transferTransaction = new Transaction();

        BankAccount toBankAcc = bankAccountService.getById(toAccId);
        BankAccount fromBankAcc = bankAccountService.getById(fromAccId);

        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        transferTransaction.setBankAccount(toBankAcc);
        transferTransaction.setCategory(transaction.getCategory());
        if (transaction.getJottings().isEmpty()) {
            transferTransaction.setJottings("Transfer transaction from " + fromBankAcc.getName());
        } else {
            transferTransaction.setJottings(transaction.getJottings());
        }
        transferTransaction.setAmount(transaction.getAmount());
        transferTransaction.setDate(format.format(new Date()));
        transferTransaction.setTypeTransaction(TypeTransaction.INCOME);

        transactionDao.persist(transferTransaction);
        bankAccountLogic(toBankAcc, transferTransaction);

        transaction.setTypeTransaction(TypeTransaction.EXPENSE);
        bankAccountLogic(fromBankAcc, transaction);

        toBankAcc.getTransactions().add(transferTransaction);
        bankAccountDao.update(toBankAcc);
        return transaction;
    }

    public Transaction update(int id, Transaction t) throws TransactionNotFoundException, NotAuthenticatedClient {
        Transaction transaction = getById(id);
        BankAccount transBankAcc = transaction.getBankAccount();

        updatedTransactionLogic(transaction, t, transBankAcc);

        transaction.setAmount(t.getAmount());
        transaction.setDate(t.getDate());
        transaction.setJottings(t.getJottings());
        return transactionDao.update(transaction);
    }

    public Transaction updateTransactionType(int id, TypeTransaction typeTransaction) throws TransactionNotFoundException, NotAuthenticatedClient {
        Transaction transaction = getById(id);
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

    public void remove(int id) throws TransactionNotFoundException, NotAuthenticatedClient {
        Transaction transaction = getById(id);
        transaction.setCategory(null);
        transaction.setBankAccount(null);
        transactionDao.remove(transaction);
    }

    public void removeFromCategory(int transId) throws TransactionNotFoundException, NotAuthenticatedClient {
        Transaction t = getById(transId);
        t.setCategory(null);
        transactionDao.update(t);
    }

    //todo sql
    private boolean isOwnerOfTransaction(Transaction t) throws NotAuthenticatedClient {
        User user = userService.isLogged();
        List<BankAccount> bankAccounts = user.getAvailableBankAccounts();
        for (BankAccount bankAccount : bankAccounts) {
            if (bankAccount.getId() == t.getBankAccount().getId()) {
                return true;
            }
        }
        return false;
    }
}
