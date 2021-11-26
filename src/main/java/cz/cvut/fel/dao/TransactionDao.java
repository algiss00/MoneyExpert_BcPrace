package cz.cvut.fel.dao;

import cz.cvut.fel.dto.TypeTransaction;
import cz.cvut.fel.model.Transaction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class TransactionDao extends AbstractDao<Transaction> {
    TransactionDao(EntityManager em) {
        super(em);
    }

    @Override
    public Transaction find(int id) {
        return em.find(Transaction.class, id);
    }

    @Override
    public List<Transaction> findAll() {
        return em.createNamedQuery("Transaction.getAll", Transaction.class).getResultList();
    }

    /**
     * Returns all transactions from BankAccount that belong to a certain Category and are in a certain period.
     *
     * @param categoryId - categoryId
     * @param accountId
     * @param month
     * @param year
     * @return
     */
    public List<Transaction> getAllTransFromCategory(int categoryId, int accountId, int month, int year) {
        try {
            return em.createNativeQuery("SELECT * from transaction_table as t " +
                            "where MONTH(t.date) = :month and YEAR(t.date) = :year " +
                            "and t.bank_account_id = :bankAccId and t.category_id = :categoryId order by t.date desc",
                    Transaction.class)
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("bankAccId", accountId)
                    .setParameter("categoryId", categoryId)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * get All transactions from BankAccount.
     *
     * @param accountId
     * @param transId
     * @return
     */
    public Transaction getFromBankAcc(int accountId, int transId) {
        return em.createNamedQuery("Transaction.getFromBankAccount", Transaction.class)
                .setParameter("bankAccId", accountId)
                .setParameter("transId", transId)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    /**
     * get All transactions from BankAccount by TypeTransaction.
     *
     * @param bankAccountId
     * @param typeTransaction
     * @return
     */
    public List<Transaction> getFromBankAccByTransactionType(int bankAccountId, TypeTransaction typeTransaction) {
        return em.createNamedQuery("Transaction.getByTransactionType", Transaction.class)
                .setParameter("type", typeTransaction)
                .setParameter("bankAccId", bankAccountId)
                .getResultList();
    }

    /**
     * Get all transactions from BankAccount, which belongs to defined Category.
     *
     * @param bankAccountId
     * @param categoryId
     * @return
     */
    public List<Transaction> getAllTransactionsByCategory(int bankAccountId, int categoryId) {
        return em.createNamedQuery("Transaction.getAllTransactionsByCategory", Transaction.class)
                .setParameter("bankAccId", bankAccountId)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    /**
     * Get all transactions from BankAccount, which belongs to Category and have TypeTransaction.
     *
     * @param bankAccountId
     * @param categoryId
     * @param typeTransaction
     * @return
     */
    public List<Transaction> getAllTransactionsByCategoryAndType(int bankAccountId, int categoryId, TypeTransaction typeTransaction) {
        return em.createNamedQuery("Transaction.getAllTransactionsByCategoryAndType", Transaction.class)
                .setParameter("bankAccId", bankAccountId)
                .setParameter("categoryId", categoryId)
                .setParameter("type", typeTransaction)
                .getResultList();
    }

    /**
     * get all transactions from BankAccount sorted by Date - desc.
     *
     * @param bankAccId
     * @return
     */
    public List<Transaction> getAllTransactionsFromBankAccSortedByDate(int bankAccId) {
        return em.createNamedQuery("Transaction.getAllFromBankAccount", Transaction.class)
                .setParameter("bankAccId", bankAccId)
                .getResultList();
    }

    // Tahle funkce zatim vynehana, ale mozna bude potreba v budoucnu
//    public List<Transaction> getAllSortedFromBankAcc(SortAttribute by, SortOrder order, BankAccount bankAccId) throws Exception {
//        try {
//            CriteriaBuilder builder = em.getCriteriaBuilder();
//            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
//            Root<Transaction> transactions = criteria.from(Transaction.class);
//            Path<?> column = transactions.get(by.getColumnName());
//            Order ordering = (order == SortOrder.ASCENDING)
//                    ? builder.asc(column) : builder.desc(column);
//
//            Predicate transactionsFromBankAcc = builder.equal(transactions.get("bankAccount"), bankAccId);
//
//            criteria.select(transactions).where(transactionsFromBankAcc).orderBy(ordering);
//            TypedQuery<Transaction> query = em.createQuery(criteria);
//            return query.getResultList();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw new Exception("Exception transaction dao");
//        }
//    }

    /**
     * get All Transactions from BankAccount sorted by month and year.
     *
     * @param month
     * @param year
     * @param bankAccId
     * @return
     */
    public List<Transaction> getByMonthSortedAndYear(int month, int year, int bankAccId) {
        try {
            return em.createNativeQuery("SELECT * from transaction_table as t " +
                            "where MONTH(t.date) = :month and YEAR(t.date) = :year and t.bank_account_id = :bankAccId order by t.date desc",
                    Transaction.class)
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("bankAccId", bankAccId)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * returns all transactions from BankAccount by TypeTransaction in a certain period.
     *
     * @param bankAccId
     * @param type
     * @param month
     * @param year
     * @return
     */
    public List<Transaction> getTransactionsByType(int bankAccId, TypeTransaction type, int month, int year) {
        try {
            return em.createNativeQuery("SELECT * from transaction_table as t " +
                            "where MONTH(t.date) = :month and YEAR(t.date) = :year " +
                            "and t.bank_account_id = :bankAccId and t.type_transaction = :typeTrans order by t.date desc",
                    Transaction.class)
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("bankAccId", bankAccId)
                    .setParameter("typeTrans", type.toString())
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * returns all transactions from BankAccount by category and type in a certain period.
     *
     * @param categoryId
     * @param bankAccId
     * @param type
     * @param month
     * @param year
     * @return
     */
    public List<Transaction> getTransactionsByTypeAndCategory(int categoryId, int bankAccId, TypeTransaction type, int month, int year) {
        try {
            return em.createNativeQuery("SELECT * from transaction_table as t " +
                            "where MONTH(t.date) = :month and YEAR(t.date) = :year " +
                            "and t.bank_account_id = :bankAccId " +
                            "and t.type_transaction = :type and t.category_id = :categoryId order by t.date desc",
                    Transaction.class)
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("bankAccId", bankAccId)
                    .setParameter("type", type.toString())
                    .setParameter("categoryId", categoryId)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Returns the sum of all expenses between date period from bankAccount.
     *
     * @param from
     * @param to
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public double getExpenseSum(Date from, Date to, int bankAccId) {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where t.date BETWEEN :from AND :to and t.bank_account_id = :bankAccId and t.type_transaction = 'EXPENSE'")
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .setParameter("bankAccId", bankAccId)
                    .getSingleResult();
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * Returns the sum of all incomes between date period from bankAccount.
     *
     * @param from
     * @param to
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public double getIncomeSum(Date from, Date to, int bankAccId) {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where t.date BETWEEN :from AND :to and t.bank_account_id = :bankAccId and t.type_transaction = 'INCOME'")
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .setParameter("bankAccId", bankAccId)
                    .getSingleResult();
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * Returns the sum of all expenses that belong to the Category, between date period from bankAccount.
     *
     * @param from
     * @param to
     * @param bankAccId
     * @param categoryId
     * @return
     * @throws Exception
     */
    public double getExpenseSumWithCategory(Date from, Date to, int bankAccId, int categoryId) {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where t.date BETWEEN :from AND :to and t.bank_account_id = :bankAccId and t.type_transaction = 'EXPENSE' " +
                    "and t.category_id = :categoryId")
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .setParameter("bankAccId", bankAccId)
                    .setParameter("categoryId", categoryId)
                    .getSingleResult();
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * Returns the sum of all incomes that belong to a Category in a certain period from bankAccount.
     *
     * @param month
     * @param year
     * @param bankAccId
     * @param categoryId
     * @return
     * @throws Exception
     */
    public double getIncomeSumWithCategory(int month, int year, int bankAccId, int categoryId) {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where MONTH(t.date) = :month and YEAR(t.date) = :year and t.bank_account_id = :bankAccId and t.type_transaction = 'INCOME' " +
                    "and t.category_id = :categoryId")
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("bankAccId", bankAccId)
                    .setParameter("categoryId", categoryId)
                    .getSingleResult();
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * get Transactions which date is between some date.
     *
     * @param from          - date from
     * @param to            - date to
     * @param bankAccountId
     * @return
     */
    public List<Transaction> getBetweenDate(Date from, Date to, int bankAccountId) {
        try {
            return em.createNativeQuery("SELECT * from transaction_table as t where t.bank_account_id = :bankAccId " +
                            "and t.date BETWEEN :from AND :to order by t.date desc",
                    Transaction.class)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .setParameter("bankAccId", bankAccountId)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Returns all transactions from BankAccount that belong to a certain Category and are in a between date.
     *
     * @param categoryId - categoryId
     * @param accountId
     * @param from
     * @param to
     * @return
     */
    public List<Transaction> getAllTransFromCategoryBetweenDate(int categoryId, int accountId, Date from, Date to) {
        try {
            return em.createNativeQuery("SELECT * from transaction_table as t " +
                            "where t.date BETWEEN :from AND :to " +
                            "and t.bank_account_id = :bankAccId and t.category_id = :categoryId order by t.date desc",
                    Transaction.class)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .setParameter("bankAccId", accountId)
                    .setParameter("categoryId", categoryId)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * returns all transactions from BankAccount by TypeTransaction and are in a between date.
     *
     * @param bankAccId
     * @param type
     * @param from
     * @param to
     * @return
     */
    public List<Transaction> getTransactionsByTypeBetweenDate(int bankAccId, TypeTransaction type, Date from, Date to) {
        try {
            return em.createNativeQuery("SELECT * from transaction_table as t " +
                            "where t.date BETWEEN :from AND :to " +
                            "and t.bank_account_id = :bankAccId and t.type_transaction = :typeTrans order by t.date desc",
                    Transaction.class)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .setParameter("bankAccId", bankAccId)
                    .setParameter("typeTrans", type.toString())
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * returns all transactions from BankAccount by category and type and are in a between date.
     *
     * @param categoryId
     * @param bankAccId
     * @param type
     * @param from
     * @param to
     * @return
     */
    public List<Transaction> getTransactionsByTypeAndCategoryBetweenDate(int categoryId, int bankAccId,
                                                                         TypeTransaction type, Date from, Date to) {
        try {
            return em.createNativeQuery("SELECT * from transaction_table as t " +
                            "where t.date BETWEEN :from AND :to " +
                            "and t.bank_account_id = :bankAccId " +
                            "and t.type_transaction = :type and t.category_id = :categoryId order by t.date desc",
                    Transaction.class)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .setParameter("bankAccId", bankAccId)
                    .setParameter("type", type.toString())
                    .setParameter("categoryId", categoryId)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Transaction persist(Transaction entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
        return entity;
    }

    @Override
    public Transaction update(Transaction entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(Transaction entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
