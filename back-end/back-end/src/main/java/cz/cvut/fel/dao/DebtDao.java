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

    public List<Debt> getNotifyDebts() {
        return em.createNamedQuery("Debt.getNotifyDebts", Debt.class).getResultList();
    }

    public List<Debt> getDeadlineDebts() {
        return em.createNamedQuery("Debt.getDeadlineDebts", Debt.class).getResultList();
    }

    public Debt getByName(int uid, String debtName) {
        return em.createNamedQuery("Debt.getByName", Debt.class)
                .setParameter("uid", uid)
                .setParameter("debtName", debtName)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
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
