package cz.cvut.fel.dao;

import cz.cvut.fel.model.Debt;
import cz.cvut.fel.model.Notify;
import cz.cvut.fel.model.TypeNotification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class NotifyDao extends AbstractDao<Notify> {
    NotifyDao(EntityManager em) {
        super(em);
    }

    @Override
    public Notify find(int id) {
        return em.find(Notify.class, id);
    }

    @Override
    public List<Notify> findAll() {
        return em.createNamedQuery("Notify.getAll").getResultList();
    }

    public Notify alreadyExistsDebt(int debtId, TypeNotification type) {
        return em.createNamedQuery("Notify.alreadyExists", Notify.class)
                .setParameter("debtId", debtId)
                .setParameter("type", type)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    @Override
    public void persist(Notify entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    @Override
    public Notify update(Notify entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(Notify entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
