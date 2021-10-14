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
        return bankAccountDao.getByName(name, isLogged().getId());
    }

    public List<Budget> getAllAccountsBudgets(int accId) throws Exception {
        BankAccount b = getByIdBankAccount(accId);
        return b.getBudgets();
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
        BankAccount b = getByIdBankAccount(accId);
        return b.getOwners();
    }

    public BankAccount persist(BankAccount bankAccount) throws Exception {
        Objects.requireNonNull(bankAccount);
        if (!validate(bankAccount))
            throw new NotValidDataException("bankAccount");
        User u = isLogged();
        bankAccount.getOwners().add(u);
        BankAccount persistedEntity = bankAccountDao.persist(bankAccount);
        u.getAvailableBankAccounts().add(bankAccount);
        userDao.update(u);
        if (bankAccount.getBalance() != 0) {
            createStartTransaction(bankAccount);
        }
        return persistedEntity;
    }

    public void addNewOwner(int userId, int accId) throws Exception {
        BankAccount b = getByIdBankAccount(accId);
        User u = getByIdUser(userId);

        b.getOwners().add(u);
        u.getAvailableBankAccounts().add(b);
        bankAccountDao.update(b);
        userDao.update(u);
    }

    private boolean validate(BankAccount bankAccount) {
        return !bankAccount.getName().trim().isEmpty();
    }

    public BankAccount updateAccount(int id, BankAccount bankAccount) throws Exception {
        BankAccount b = getByIdBankAccount(id);
        b.setName(bankAccount.getName());
        b.setCurrency(bankAccount.getCurrency());
        return bankAccountDao.update(b);
    }

    public void remove(int id) throws Exception {
        BankAccount bankAccount = getByIdBankAccount(id);
        bankAccount.getOwners().clear();
        bankAccountDao.remove(bankAccount);
    }

    public void removeOwner(int userId, int accId) throws Exception {
        BankAccount b = getByIdBankAccount(accId);
        User u = getByIdUser(userId);

        b.getOwners().remove(u);
        u.getAvailableBankAccounts().remove(b);
        bankAccountDao.update(b);
        userDao.update(u);
    }

    public void removeBudgetFromBankAcc(int budgetId, int accId) throws Exception {
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

    private boolean isBudgetInBankAcc(BankAccount bankAccount, Budget budget) throws Exception {
        return budgetDao.getByBankAcc(budget.getId(), bankAccount.getId()) != null;
    }

    private boolean isDebtInBankAcc(BankAccount bankAccount, Debt d) throws Exception {
        return debtDao.getByBankAccId(d.getId(), bankAccount.getId()) != null;
    }

    public void removeDebt(int id, int accId) throws Exception {
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

    public void removeAllTrans(int accId) throws Exception {
        BankAccount b = getByIdBankAccount(accId);
        for (Transaction t : b.getTransactions()) {
            removeTransFromAccount(t.getId(), b.getId());
        }
    }


    private void createStartTransaction(BankAccount bankAccount) throws Exception {
        Transaction startTransaction = new Transaction();
        Category category = getByIdCategory(-6);

        startTransaction.setBankAccount(bankAccount);
        startTransaction.setCategory(category);
        startTransaction.setJottings("Start transaction");
        startTransaction.setAmount(bankAccount.getBalance());
        startTransaction.setDate(new Date());
        startTransaction.setTypeTransaction(TypeTransaction.INCOME);

        transactionDao.persist(startTransaction);
    }
}
