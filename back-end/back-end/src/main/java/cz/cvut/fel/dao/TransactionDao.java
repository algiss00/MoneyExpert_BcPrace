package cz.cvut.fel.dao;

import cz.cvut.fel.model.Category;
import cz.cvut.fel.model.Transaction;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class TransactionDao extends AbstractDao<Transaction> {
    TransactionDao(EntityManager em) {
        super(em);
    }

    @Override
    public Transaction find(String id) {
        return em.find(Transaction.class, id);
    }

    @Override
    public List<Transaction> findAll() {
        return em.createNamedQuery("Transaction.getAll").getResultList();
    }

    public List<Transaction> getAllTransFromCategory(String catId, String accountId) {
        return em.createNamedQuery("Transaction.getAllFromCategory", Transaction.class)
                .setParameter("catId", catId)
                .setParameter("accId", accountId)
                .getResultList();
    }

    public Transaction getFromBankAcc(String accountId, String transId) {
        return em.createNamedQuery("Transaction.getFromBankAccount", Transaction.class)
                .setParameter("bankAccId", accountId)
                .setParameter("transId", transId)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
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
