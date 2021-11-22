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

    /**
     * get by name users available bankAccounts.
     *
     * @param name
     * @return
     * @throws Exception
     */
    public List<BankAccount> getByNameAvailableBankAcc(String name) throws Exception {
        return bankAccountDao.getByNameAvailableBankAcc(name, getAuthenticatedUser().getId());
    }

    /**
     * get by name users created bankAccounts.
     *
     * @param name
     * @return
     * @throws Exception
     */
    public List<BankAccount> getByNameCreatedBankAcc(String name) throws Exception {
        return bankAccountDao.getByNameCreated(name, getAuthenticatedUser().getId());
    }

    /**
     * get all budgets from bankAccount.
     *
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public List<Budget> getAllAccountsBudgets(int bankAccId) throws Exception {
        BankAccount byIdBankAccount = getByIdBankAccount(bankAccId);
        return byIdBankAccount.getBudgets();
    }

    /**
     * get all transactions from bankAccount.
     *
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public List<Transaction> getAllTransactions(int bankAccId) throws Exception {
        return transactionDao.getAllTransactionsFromBankAccSortedByDate(getByIdBankAccount(bankAccId).getId());
    }

    /**
     * get all transactions from bankAccount by type.
     *
     * @param bankAccId
     * @param typeTransaction
     * @return
     * @throws Exception
     */
    public List<Transaction> getAllTransactionsByType(int bankAccId, TypeTransaction typeTransaction) throws Exception {
        return transactionDao.getFromBankAccByTransactionType(getByIdBankAccount(bankAccId).getId(), typeTransaction);
    }

    /**
     * get all transactions from bankAccount by category.
     *
     * @param bankAccId
     * @param categoryId
     * @return
     * @throws Exception
     */
    public List<Transaction> getAllTransactionsByCategory(int bankAccId, int categoryId) throws Exception {
        return transactionDao.getAllTransactionsByCategory(getByIdBankAccount(bankAccId).getId(), getByIdCategory(categoryId).getId());
    }

    /**
     * get all transactions from bankAccount by category and type.
     *
     * @param bankAccId
     * @param categoryId
     * @param type
     * @return
     * @throws Exception
     */
    public List<Transaction> getAllTransactionsByCategoryAndType(int bankAccId, int categoryId, TypeTransaction type) throws Exception {
        return transactionDao.getAllTransactionsByCategoryAndType(getByIdBankAccount(bankAccId).getId(),
                getByIdCategory(categoryId).getId(), type);
    }

    /**
     * get all debts from bankAccount.
     *
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public List<Debt> getAllAccountsDebts(int bankAccId) throws Exception {
        return debtDao.getSortedByDeadlineFromBankAcc(getByIdBankAccount(bankAccId).getId());
    }

    /**
     * get all owners of bankAccount.
     *
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public List<User> getAllOwners(int bankAccId) throws Exception {
        BankAccount byIdBankAccount = getByIdBankAccount(bankAccId);
        return byIdBankAccount.getOwners();
    }

    /**
     * get creator of bankAccount.
     *
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public User getCreator(int bankAccId) throws Exception {
        BankAccount byIdBankAccount = getByIdBankAccount(bankAccId);
        return byIdBankAccount.getCreator();
    }

    /**
     * Persist bankAccount.
     * if Balance < 0, then not valid
     * if Balance > 0, then will be created start transaction with amount of balance
     * if Balance == 0, then will be not created start transaction
     *
     * @param bankAccount
     * @return
     * @throws Exception
     */
    public BankAccount persist(BankAccount bankAccount) throws Exception {
        Objects.requireNonNull(bankAccount);
        if (!validate(bankAccount))
            throw new NotValidDataException("persist bankAccount");
        User u = getAuthenticatedUser();
        bankAccount.setCreator(u);
        BankAccount persistedEntity = bankAccountDao.persist(bankAccount);
        u.getCreatedBankAccounts().add(bankAccount);
        userDao.update(u);
        if (bankAccount.getBalance() != 0) {
            Transaction startTransaction = createStartTransaction(bankAccount);
            persistedEntity.getTransactions().add(startTransaction);
            bankAccountDao.update(persistedEntity);
        }
        return persistedEntity;
    }

    /**
     * Add new owner to BankAccount.
     * Only the Creator of BankAcc has the right to add a new owner
     * cannot be added to an existing user
     *
     * @param username
     * @param accId    - bankAcc Id
     * @throws Exception
     */
    public void addNewOwner(String username, int accId) throws Exception {
        BankAccount bankAccount = getByIdBankAccount(accId);
        if (bankAccount.getCreator() != getAuthenticatedUser()) {
            throw new NotValidDataException("You are not creator!");
        }

        User user = getByUsername(username);
        if (user == null) {
            throw new UserNotFoundException(username);
        }

        if (bankAccount.getOwners().contains(user) || bankAccount.getCreator() == user) {
            throw new NotValidDataException("Add existed owner!");
        }
        bankAccount.getOwners().add(user);
        user.getAvailableBankAccounts().add(bankAccount);
        bankAccountDao.update(bankAccount);
        userDao.update(user);
    }

    /**
     * validation bankAccount.
     *
     * @param bankAccount
     * @return
     */
    private boolean validate(BankAccount bankAccount) {
        if (bankAccount.getName().trim().isEmpty() || bankAccount.getCurrency() == null
                || bankAccount.getBalance() == null || bankAccount.getBalance() < 0) {
            return false;
        }
        return true;
    }

    /**
     * Update BankAccount.
     * Only the creator of BankAcc has the right to update BankAccount
     *
     * @param id
     * @param bankAccount
     * @return
     * @throws Exception
     */
    public BankAccount updateAccount(int id, BankAccount bankAccount) throws Exception {
        BankAccount byIdBankAccount = getByIdBankAccount(id);
        if (byIdBankAccount.getCreator() != getAuthenticatedUser()) {
            throw new NotValidDataException("Cannot update BankAccount, you are not creator!");
        }
        byIdBankAccount.setName(bankAccount.getName());
        byIdBankAccount.setCurrency(bankAccount.getCurrency());
        byIdBankAccount.setBalance(bankAccount.getBalance());
        return bankAccountDao.update(byIdBankAccount);
    }

    /**
     * Remove a owner from bankAccount.
     * Cannot remove not existing owner and creator of BankAcc
     * Only the Creator is authorized to remove a owner
     *
     * @param userId
     * @param accId
     * @throws Exception
     */
    public void removeOwner(int userId, int accId) throws Exception {
        BankAccount bankAccount = getByIdBankAccount(accId);
        if (bankAccount.getCreator() != getAuthenticatedUser()) {
            throw new NotValidDataException("You are not creator!");
        }
        User user = getByIdUser(userId);

        // not allowed to delete creator of BankAccount
        if (!isUserOwnerOfBankAccount(user, bankAccount) || bankAccount.getCreator() == user) {
            throw new NotAuthenticatedClient();
        }

        bankAccount.getOwners().remove(user);
        user.getAvailableBankAccounts().remove(bankAccount);
        bankAccountDao.update(bankAccount);
        userDao.update(user);
    }

    /**
     * Remove budget from BankAccount.
     * Remove the Budget from BankAccount then the budget itself from Db
     *
     * @param budgetId
     * @param bankAccId
     * @throws Exception
     */
    public void removeBudgetFromBankAcc(int budgetId, int bankAccId) throws Exception {
        Budget budget = getByIdBudget(budgetId);
        BankAccount bankAccount = getByIdBankAccount(bankAccId);
        if (!isBudgetInBankAcc(budget, bankAccount)) {
            throw new NotAuthenticatedClient();
        }
        bankAccount.getBudgets().remove(budget);
        bankAccountDao.update(bankAccount);
        removeBudget(budget.getId());
    }

    /**
     * check if budget belongs to the bankAccount.
     *
     * @param budget
     * @param bankAccount
     * @return
     */
    private boolean isBudgetInBankAcc(Budget budget, BankAccount bankAccount) {
        return budgetDao.getByBankAcc(budget.getId(), bankAccount.getId()) != null;
    }

    /**
     * check if debt is belongs to the BankAccount.
     *
     * @param d
     * @param bankAccount
     * @return
     */
    private boolean isDebtInBankAcc(Debt d, BankAccount bankAccount) {
        return debtDao.getByBankAccId(d.getId(), bankAccount.getId()) != null;
    }

    /**
     * Remove debt from BankAccount.
     * Remove Debt from BankAccount then Debt itself from Db
     *
     * @param debtId
     * @param bankAccId
     * @throws Exception
     */
    public void removeDebt(int debtId, int bankAccId) throws Exception {
        Debt debt = getByIdDebt(debtId);
        BankAccount bankAccount = getByIdBankAccount(bankAccId);
        if (!isDebtInBankAcc(debt, bankAccount)) {
            throw new NotAuthenticatedClient();
        }
        bankAccount.getDebts().remove(debt);
        bankAccountDao.update(bankAccount);
        removeDebt(debt.getId());
    }

    /**
     * Create Start transaction with default category "Startovni transakce".
     *
     * @param bankAccount
     * @return
     * @throws Exception
     */
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
