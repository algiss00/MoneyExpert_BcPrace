package cz.cvut.fel.dao;

import cz.cvut.fel.dto.TypeTransaction;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Transaction;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
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

    public List<Transaction> getAllTransFromCategory(int catId, int accountId) {
        return em.createNamedQuery("Transaction.getAllFromCategory", Transaction.class)
                .setParameter("catId", catId)
                .setParameter("accId", accountId)
                .getResultList();
    }

    public Transaction getFromBankAcc(int accountId, int transId) {
        try {
            return em.createNamedQuery("Transaction.getFromBankAccount", Transaction.class)
                    .setParameter("bankAccId", accountId)
                    .setParameter("transId", transId)
                    .setMaxResults(1)
                    .getResultList()
                    .stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Transaction> getFromBankAccByTransactionType(int accountId, TypeTransaction typeTransaction) {
        return em.createNamedQuery("Transaction.getByTransactionType", Transaction.class)
                .setParameter("type", typeTransaction)
                .setParameter("bankAccId", accountId)
                .getResultList();
    }

    public List<Transaction> getAllTransactionsFromBankAccByDate(int bankAccId) {
        return em.createNamedQuery("Transaction.getAllFromBankAccount", Transaction.class)
                .setParameter("bankAccId", bankAccId)
                .getResultList();
    }

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

    public List<Transaction> getByMonthSorted(int month, int year, int bankAccId) {
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

    public double getExpenseSum(int month, int year, int bankAccId) throws Exception {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where MONTH(t.date) = :month and YEAR(t.date) = :year and t.bank_account_id = :bankAccId and t.type_transaction = 'EXPENSE'")
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("bankAccId", bankAccId)
                    .getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Internal error");
        }
    }

    public double getIncomeSum(int month, int year, int bankAccId) throws Exception {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where MONTH(t.date) = :month and YEAR(t.date) = :year and t.bank_account_id = :bankAccId and t.type_transaction = 'INCOME'")
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("bankAccId", bankAccId)
                    .getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Internal error");
        }
    }

    public double getExpenseSumWithCategory(int month, int year, int bankAccId, int catId) throws Exception {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where MONTH(t.date) = :month and YEAR(t.date) = :year and t.bank_account_id = :bankAccId and t.type_transaction = 'EXPENSE' " +
                    "and t.category_id = :categoryId")
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("bankAccId", bankAccId)
                    .setParameter("categoryId", catId)
                    .getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Internal error");
        }
    }

    public double getIncomeSumWithCategory(int month, int year, int bankAccId, int catId) throws Exception {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where MONTH(t.date) = :month and YEAR(t.date) = :year and t.bank_account_id = :bankAccId and t.type_transaction = 'INCOME' " +
                    "and t.category_id = :categoryId")
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("bankAccId", bankAccId)
                    .setParameter("categoryId", catId)
                    .getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Internal error");
        }
    }

    public List<Transaction> getBetweenDate(String from, String to, BankAccount idBankAccount) {
        try {
            return em.createNativeQuery("SELECT * from transaction_table as t where t.bank_account_id = :bankAccId " +
                            "and t.date BETWEEN :from AND :to ",
                    Transaction.class)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .setParameter("bankAccId", idBankAccount)
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
