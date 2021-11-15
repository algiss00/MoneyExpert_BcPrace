package cz.cvut.fel.dao;

import cz.cvut.fel.dto.TypeTransaction;
import cz.cvut.fel.model.Transaction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
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
     * Vrati vse transacke z BankAccount, ktere patri k urcite Category a jsou v urcitem obdobi
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
     * get All transactions from BankAccount
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
     * get All transactions from BankAccount by TypeTransaction
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
     * Get all transactions from BankAccount, which belongs to defined Category
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
     * Get all transactions from BankAccount, which belongs to Category and have TypeTransaction
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
     * get all transactions from BankAccount sorted by Date - desc
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
     * get All Transactions from BankAccount sorted by month and year
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
     * vrati vse transakce z BankAccount podle TypeTransaction v urcitem obdobi
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
     * vrati vse transakce z BankAccount podle category a typu zaroven v urcitem obdobi
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
     * Vrati soucet vsech vydaju v urcitem obdobi z bankAccount
     *
     * @param month
     * @param year
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public double getExpenseSum(int month, int year, int bankAccId) {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where MONTH(t.date) = :month and YEAR(t.date) = :year and t.bank_account_id = :bankAccId and t.type_transaction = 'EXPENSE'")
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("bankAccId", bankAccId)
                    .getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Vrati soucet vsech prijmu v urcitem obdobi z bankAccount
     *
     * @param month
     * @param year
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public double getIncomeSum(int month, int year, int bankAccId) {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where MONTH(t.date) = :month and YEAR(t.date) = :year and t.bank_account_id = :bankAccId and t.type_transaction = 'INCOME'")
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("bankAccId", bankAccId)
                    .getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Vrati soucet vsech vydaju, ktere patri do Category, v urcitem obdobi z bankAccount
     *
     * @param month
     * @param year
     * @param bankAccId
     * @param categoryId
     * @return
     * @throws Exception
     */
    public double getExpenseSumWithCategory(int month, int year, int bankAccId, int categoryId) {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where MONTH(t.date) = :month and YEAR(t.date) = :year and t.bank_account_id = :bankAccId and t.type_transaction = 'EXPENSE' " +
                    "and t.category_id = :categoryId")
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("bankAccId", bankAccId)
                    .setParameter("categoryId", categoryId)
                    .getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Vrati soucet vsech prijmu, ktere patri do Category, v urcitem obdobi z bankAccount
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
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * get Transactions which date is between some date
     *
     * @param from          - date from
     * @param to            - date to
     * @param bankAccountId
     * @return
     */
    public List<Transaction> getBetweenDate(String from, String to, int bankAccountId) {
        try {
            return em.createNativeQuery("SELECT * from transaction_table as t where t.bank_account_id = :bankAccId " +
                            "and t.date BETWEEN :from AND :to",
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
