package cz.cvut.fel.dao;

import cz.cvut.fel.model.BankAccount;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class BankAccountDao extends AbstractDao<BankAccount> {

    BankAccountDao(EntityManager em) {
        super(em);
    }

    @Override
    public BankAccount find(int id) {
        return em.find(BankAccount.class, id);
    }

    @Override
    public List<BankAccount> findAll() {
        return em.createNamedQuery("BankAccount.getAll").getResultList();
    }

    @Override
    public void persist(BankAccount entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    @Override
    public BankAccount update(BankAccount entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(BankAccount entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
