package cz.cvut.fel.dao;

import cz.cvut.fel.model.NotifyBudget;
import cz.cvut.fel.model.TypeNotification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class NotifyBudgetDao extends AbstractDao<NotifyBudget> {
    NotifyBudgetDao(EntityManager em) {
        super(em);
    }

    @Override
    public NotifyBudget find(int id) {
        return em.find(NotifyBudget.class, id);
    }

    @Override
    public List<NotifyBudget> findAll() {
        return em.createNamedQuery("NotifyBudget.getAll").getResultList();
    }

    public NotifyBudget alreadyExistsDebt(int debtId, TypeNotification type) {
        return em.createNamedQuery("NotifyBudget.alreadyExists", NotifyBudget.class)
                .setParameter("budgetId", debtId)
                .setParameter("type", type)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    @Override
    public void persist(NotifyBudget entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    @Override
    public NotifyBudget update(NotifyBudget entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(NotifyBudget entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
