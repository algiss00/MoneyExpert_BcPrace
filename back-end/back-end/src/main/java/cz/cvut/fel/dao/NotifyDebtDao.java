package cz.cvut.fel.dao;

import cz.cvut.fel.model.NotifyDebt;
import cz.cvut.fel.model.TypeNotification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class NotifyDebtDao extends AbstractDao<NotifyDebt> {
    NotifyDebtDao(EntityManager em) {
        super(em);
    }

    @Override
    public NotifyDebt find(String id) {
        return em.find(NotifyDebt.class, id);
    }

    @Override
    public List<NotifyDebt> findAll() {
        return em.createNamedQuery("Notify.getAll").getResultList();
    }

    public NotifyDebt alreadyExistsDebt(String debtId, TypeNotification type) {
        return em.createNamedQuery("Notify.alreadyExists", NotifyDebt.class)
                .setParameter("debtId", debtId)
                .setParameter("type", type)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    @Override
    public void persist(NotifyDebt entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    @Override
    public NotifyDebt update(NotifyDebt entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(NotifyDebt entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
