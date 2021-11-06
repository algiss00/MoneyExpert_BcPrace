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

    public List<BankAccount> getByNameAvailableBankAcc(String name) throws Exception {
        return bankAccountDao.getByNameAvailableBankAcc(name, getAuthenticatedUser().getId());
    }

    public List<BankAccount> getByNameCreatedBankAcc(String name) throws Exception {
        return bankAccountDao.getByNameCreated(name, getAuthenticatedUser().getId());
    }

    public List<Budget> getAllAccountsBudgets(int bankAccId) throws Exception {
        BankAccount byIdBankAccount = getByIdBankAccount(bankAccId);
        return byIdBankAccount.getBudgets();
    }

    public List<Transaction> getAllTransactions(int bankAccId) throws Exception {
        return transactionDao.getAllTransactionsFromBankAccSortedByDate(getByIdBankAccount(bankAccId).getId());
    }

    public List<Transaction> getAllTransactionsByType(int bankAccId, TypeTransaction typeTransaction) throws Exception {
        return transactionDao.getFromBankAccByTransactionType(getByIdBankAccount(bankAccId).getId(), typeTransaction);
    }

    public List<Transaction> getAllTransactionsByCategory(int bankAccId, int categoryId) throws Exception {
        return transactionDao.getAllTransactionsByCategory(getByIdBankAccount(bankAccId).getId(), getByIdCategory(categoryId).getId());
    }

    public List<Transaction> getAllTransactionsByCategoryAndType(int bankAccId, int categoryId, TypeTransaction type) throws Exception {
        return transactionDao.getAllTransactionsByCategoryAndType(getByIdBankAccount(bankAccId).getId(),
                getByIdCategory(categoryId).getId(), type);
    }

    public List<Debt> getAllAccountsDebts(int bankAccId) throws Exception {
        return debtDao.getSortedByDeadlineFromBankAcc(getByIdBankAccount(bankAccId).getId());
    }

    public List<User> getAllOwners(int bankAccId) throws Exception {
        BankAccount byIdBankAccount = getByIdBankAccount(bankAccId);
        return byIdBankAccount.getOwners();
    }

    public User getCreator(int bankAccId) throws Exception {
        BankAccount byIdBankAccount = getByIdBankAccount(bankAccId);
        return byIdBankAccount.getCreator();
    }

    /**
     * pokud Balance je < 0, tehdy not valid
     * pokud Balance > 0, tehdy se vytvori startovni transakce s amount balance
     * pokud Balance == 0, tehdy se nevytvori startovni transakce
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
     * Pridani noveho ownera ma pravo pouze Creator of BankAcc
     * nelze pridat uz existujiciho uzivatele
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

    private boolean validate(BankAccount bankAccount) {
        if (bankAccount.getName().trim().isEmpty() || bankAccount.getCurrency() == null
                || bankAccount.getBalance() == null || bankAccount.getBalance() < 0) {
            return false;
        }
        return true;
    }

    /**
     * Na update BankAcc ma pravo pouze creator of BankAcc
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
     * Cannot remove not existed owner and creator of BankAcc
     * Ma pravo pouze Creator
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
     * Odstarni Budget z BankAccount potom samotny budget z Db
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

    private boolean isBudgetInBankAcc(Budget budget, BankAccount bankAccount) {
        return budgetDao.getByBankAcc(budget.getId(), bankAccount.getId()) != null;
    }

    private boolean isDebtInBankAcc(Debt d, BankAccount bankAccount) {
        return debtDao.getByBankAccId(d.getId(), bankAccount.getId()) != null;
    }

    /**
     * Odstrani Debt z BankAccount potom samotny Debt z Db
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
     * Vytvareni Startovni transakce
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
