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
    public Transaction find(int id) {
        return em.find(Transaction.class, id);
    }

    @Override
    public List<Transaction> findAll() {
        return em.createNamedQuery("Transaction.getAll").getResultList();
    }

    public List<Transaction> getAllFromAccount(int accountId) {
        return em.createNamedQuery("Transaction.getAllFromAccount", Transaction.class)
                .setParameter("id", accountId)
                .getResultList();
    }

    //todo
//    public List<Transaction> getAllTransFromCategory(Category cat, int accountId) {
//        return em.createNamedQuery("Transaction.getAllFromCategory", Transaction.class)
//                .setParameter("cat", cat)
//                .setParameter("accId", accountId)
//                .getResultList();
//    }

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
