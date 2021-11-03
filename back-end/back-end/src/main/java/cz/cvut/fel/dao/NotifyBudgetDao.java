package cz.cvut.fel.dao;

import cz.cvut.fel.model.NotifyBudget;
import cz.cvut.fel.dto.TypeNotification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
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
        return em.createNamedQuery("NotifyBudget.getAll", NotifyBudget.class).getResultList();
    }

    /**
     * get All budgets with Notifybudget
     *
     * @param budgetId
     * @return
     */
    public List<NotifyBudget> getBudgetsNotifyBudgets(int budgetId) {
        return em.createNamedQuery("NotifyBudget.getBudgetsNotifyBudgets", NotifyBudget.class)
                .setParameter("budgetId", budgetId)
                .getResultList();
    }

    /**
     * get budget with Notifybudget by Type
     *
     * @param budgetId
     * @return
     */
    public NotifyBudget getBudgetsNotifyBudgetByType(int budgetId, TypeNotification typeNotification) {
        return em.createNamedQuery("NotifyBudget.getBudgetsNotifyBudgetByType", NotifyBudget.class)
                .setParameter("budgetId", budgetId)
                .setParameter("typeNotif", typeNotification)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    public Boolean alreadyExistsBudget(int budgetId, TypeNotification type) {
        return em.createNamedQuery("NotifyBudget.alreadyExists", NotifyBudget.class)
                .setParameter("budgetId", budgetId)
                .setParameter("type", type)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null) != null;
    }

    public List<NotifyBudget> getNotifyBudgetByBudgetId(int budgetId) {
        return em.createNamedQuery("NotifyBudget.getNotifyBudgetByBudgetId", NotifyBudget.class)
                .setParameter("budgetId", budgetId)
                .getResultList();
    }

    public List<NotifyBudget> getNotifyBudgetsFromBankAccount(int bankAccId) {
        return em.createNamedQuery("NotifyBudget.getNotifyBudgetsFromBankAccount", NotifyBudget.class)
                .setParameter("bankAccId", bankAccId)
                .getResultList();
    }

    public void deleteNotifyBudgetByBudgetId(int budgetId) throws Exception {
        try {
            em.createNamedQuery("NotifyBudget.deleteNotifyBudgetByBudgetId")
                    .setParameter("budgetId", budgetId)
                    .executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception NotifyBudgetDao");
        }
    }

    public void deleteNotifyBudgetById(int notifyId) throws Exception {
        try {
            em.createNamedQuery("NotifyBudget.deleteNotifyBudgetById")
                    .setParameter("notifyId", notifyId)
                    .executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception NotifyBudgetDao");
        }
    }

    @Override
    public NotifyBudget persist(NotifyBudget entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
        return entity;
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
