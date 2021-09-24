package cz.cvut.fel.dao;

import cz.cvut.fel.dto.SortAttribute;
import cz.cvut.fel.dto.SortOrder;
import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Transaction;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
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
        return em.createNamedQuery("Transaction.getAll").getResultList();
    }

    public List<Transaction> getAllTransFromCategory(int catId, int accountId) {
        return em.createNamedQuery("Transaction.getAllFromCategory", Transaction.class)
                .setParameter("catId", catId)
                .setParameter("accId", accountId)
                .getResultList();
    }

    public Transaction getFromBankAcc(int accountId, int transId) {
        return em.createNamedQuery("Transaction.getFromBankAccount", Transaction.class)
                .setParameter("bankAccId", accountId)
                .setParameter("transId", transId)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    public List<Transaction> getAllSorted(SortAttribute by, SortOrder order, BankAccount bankAccId) throws Exception {
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> transactions = criteria.from(Transaction.class);
            Path<?> column = transactions.get(by.getColumnName());
            Order ordering = (order == SortOrder.ASCENDING)
                    ? builder.asc(column) : builder.desc(column);

            Predicate transactionsFromBankAcc = builder.equal(transactions.get("bankAccount"), bankAccId);

            criteria.select(transactions).where(transactionsFromBankAcc).orderBy(ordering);
            TypedQuery<Transaction> query = em.createQuery(criteria);
            return query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception transaction dao");
        }
    }

    public List<Transaction> getByMonthSorted(int month, int bankAccId) throws Exception {
        try {
            return em.createNativeQuery("SELECT * from transaction_table as t " +
                            "where MONTH(t.date) = :month and t.bank_account_id = :bankAccId order by t.date desc",
                    Transaction.class)
                    .setParameter("month", month)
                    .setParameter("bankAccId", bankAccId)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception transaction dao");
        }
    }

    public double getExpenseSum(int month, int bankAccId) throws Exception {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where MONTH(t.date) = :month and t.bank_account_id = :bankAccId and t.type_transaction = 'EXPENSE'")
                    .setParameter("month", month)
                    .setParameter("bankAccId", bankAccId)
                    .getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception transaction dao");
        }
    }

    public double getIncomeSum(int month, int bankAccId) throws Exception {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where MONTH(t.date) = :month and t.bank_account_id = :bankAccId and t.type_transaction = 'INCOME'")
                    .setParameter("month", month)
                    .setParameter("bankAccId", bankAccId)
                    .getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception transaction dao");
        }
    }

    public double getExpenseSumWithCategory(int month, int bankAccId, int catId) throws Exception {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where MONTH(t.date) = :month and t.bank_account_id = :bankAccId and t.type_transaction = 'EXPENSE' " +
                    "and t.category_id = :categoryId")
                    .setParameter("month", month)
                    .setParameter("bankAccId", bankAccId)
                    .setParameter("categoryId", catId)
                    .getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception transaction dao");
        }
    }

    public double getIncomeSumWithCategory(int month, int bankAccId, int catId) throws Exception {
        try {
            return (double) em.createNativeQuery("SELECT SUM(t.amount) from transaction_table as t " +
                    "where MONTH(t.date) = :month and t.bank_account_id = :bankAccId and t.type_transaction = 'INCOME' " +
                    "and t.category_id = :categoryId")
                    .setParameter("month", month)
                    .setParameter("bankAccId", bankAccId)
                    .setParameter("categoryId", catId)
                    .getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception transaction dao");
        }
    }

    @Override
    public void persist(Transaction entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
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
