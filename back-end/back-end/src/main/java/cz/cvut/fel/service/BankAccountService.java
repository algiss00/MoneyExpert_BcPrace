package cz.cvut.fel.service;

import cz.cvut.fel.dao.*;
import cz.cvut.fel.dto.TypeTransaction;
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

    public List<BankAccount> getByName(String name) throws Exception {
        return bankAccountDao.getByName(name, getAuthenticatedUser().getId());
    }

    public List<Budget> getAllAccountsBudgets(int accId) throws Exception {
        BankAccount byIdBankAccount = getByIdBankAccount(accId);
        return byIdBankAccount.getBudgets();
    }

    public List<Transaction> getAllTransactions(int accountId) throws Exception {
        return transactionDao.getAllTransactionsFromBankAccSortedByDate(getByIdBankAccount(accountId).getId());
    }

    public List<Transaction> getAllTransactionsByType(int accountId, TypeTransaction typeTransaction) throws Exception {
        return transactionDao.getFromBankAccByTransactionType(getByIdBankAccount(accountId).getId(), typeTransaction);
    }

    public List<Debt> getAllAccountsDebts(int accId) throws Exception {
        return debtDao.getSortedByDeadlineFromBankAcc(getByIdBankAccount(accId).getId());
    }

    public List<User> getAllOwners(int accId) throws Exception {
        BankAccount byIdBankAccount = getByIdBankAccount(accId);
        return byIdBankAccount.getOwners();
    }

    public BankAccount persist(BankAccount bankAccount) throws Exception {
        Objects.requireNonNull(bankAccount);
        if (!validate(bankAccount))
            throw new NotValidDataException("bankAccount");
        User u = getAuthenticatedUser();
        bankAccount.getOwners().add(u);
        BankAccount persistedEntity = bankAccountDao.persist(bankAccount);
        u.getAvailableBankAccounts().add(bankAccount);
        userDao.update(u);
        if (bankAccount.getBalance() != 0) {
            Transaction startTransaction = createStartTransaction(bankAccount);
            persistedEntity.getTransactions().add(startTransaction);
            bankAccountDao.update(persistedEntity);
        }
        return persistedEntity;
    }

    public void addNewOwner(int userId, int accId) throws Exception {
        BankAccount bankAccount = getByIdBankAccount(accId);
        User user = getByIdUser(userId);

        bankAccount.getOwners().add(user);
        user.getAvailableBankAccounts().add(bankAccount);
        bankAccountDao.update(bankAccount);
        userDao.update(user);
    }

    private boolean validate(BankAccount bankAccount) {
        if (bankAccount.getName().trim().isEmpty() || bankAccount.getCurrency() == null
                || bankAccount.getBalance() == null) {
            return false;
        }
        return true;
    }

    public BankAccount updateAccount(int id, BankAccount bankAccount) throws Exception {
        BankAccount byIdBankAccount = getByIdBankAccount(id);
        byIdBankAccount.setName(bankAccount.getName());
        byIdBankAccount.setCurrency(bankAccount.getCurrency());
        return bankAccountDao.update(byIdBankAccount);
    }

    public void removeOwner(int userId, int accId) throws Exception {
        BankAccount bankAccount = getByIdBankAccount(accId);
        User user = getByIdUser(userId);

        bankAccount.getOwners().remove(user);
        user.getAvailableBankAccounts().remove(bankAccount);
        bankAccountDao.update(bankAccount);
        userDao.update(user);
    }

    public void removeBudgetFromBankAcc(int budgetId, int accId) throws Exception {
        Budget budget = getByIdBudget(budgetId);
        BankAccount bankAccount = getByIdBankAccount(accId);
        if (!isBudgetInBankAcc(budget, bankAccount)) {
            throw new NotAuthenticatedClient();
        }
        bankAccount.getBudgets().remove(budget);
        budget.setBankAccount(null);
        budgetDao.update(budget);
        bankAccountDao.update(bankAccount);
    }

    private boolean isBudgetInBankAcc(Budget budget, BankAccount bankAccount) throws Exception {
        return budgetDao.getByBankAcc(budget.getId(), bankAccount.getId()) != null;
    }

    private boolean isDebtInBankAcc(Debt d, BankAccount bankAccount) throws Exception {
        return debtDao.getByBankAccId(d.getId(), bankAccount.getId()) != null;
    }

    public void removeDebt(int debtId, int accId) throws Exception {
        Debt debt = getByIdDebt(debtId);
        BankAccount bankAccount = getByIdBankAccount(accId);
        if (!isDebtInBankAcc(debt, bankAccount)) {
            throw new NotAuthenticatedClient();
        }
        bankAccount.getDebts().remove(debt);
        debt.setCreator(null);
        debt.setBankAccount(null);
        debtDao.update(debt);
        bankAccountDao.update(bankAccount);
    }

    private Transaction createStartTransaction(BankAccount bankAccount) throws Exception {
        Transaction startTransaction = new Transaction();
        Category category = getByIdCategory(-6);

        startTransaction.setBankAccount(bankAccount);
        startTransaction.setCategory(category);
        startTransaction.setJottings("Start transaction");
        startTransaction.setAmount(bankAccount.getBalance());
        startTransaction.setDate(new Date());
        startTransaction.setTypeTransaction(TypeTransaction.INCOME);

        return transactionDao.persist(startTransaction);
    }
}
