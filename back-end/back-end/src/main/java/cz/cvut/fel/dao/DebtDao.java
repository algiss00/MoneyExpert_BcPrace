package cz.cvut.fel.dao;

import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.Debt;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class DebtDao extends AbstractDao<Debt> {

    DebtDao(EntityManager em) {
        super(em);
    }

    @Override
    public Debt find(int id) {
        return em.find(Debt.class, id);
    }

    @Override
    public List<Debt> findAll() {
        return em.createNamedQuery("Debt.getAll").getResultList();
    }

    public List<Debt> getAllAccountsDebts(int accId) {
        return em.createNamedQuery("Category.getAllAccountsDebts", Debt.class)
                .setParameter("accId", accId)
                .getResultList();
    }

    public List<Debt> getAllUsersDebts(int uid) {
        return em.createNamedQuery("Category.getAllUsersDebts", Debt.class)
                .setParameter("uid", uid)
                .getResultList();
    }

    @Override
    public void persist(Debt entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    @Override
    public Debt update(Debt entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(Debt entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
