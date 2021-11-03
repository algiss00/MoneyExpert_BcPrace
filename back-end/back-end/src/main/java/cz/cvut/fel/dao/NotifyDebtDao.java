package cz.cvut.fel.dao;

import cz.cvut.fel.model.NotifyDebt;
import cz.cvut.fel.dto.TypeNotification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class NotifyDebtDao extends AbstractDao<NotifyDebt> {
    NotifyDebtDao(EntityManager em) {
        super(em);
    }

    @Override
    public NotifyDebt find(int id) {
        return em.find(NotifyDebt.class, id);
    }

    @Override
    public List<NotifyDebt> findAll() {
        return em.createNamedQuery("Notify.getAll", NotifyDebt.class).getResultList();
    }

    /**
     * get All Debts with Notify
     *
     * @param debtId
     * @return
     */
    public List<NotifyDebt> getDebtsNotifyDebts(int debtId) {
        return em.createNamedQuery("Notify.getDebtsNotifyDebts", NotifyDebt.class)
                .setParameter("debtId", debtId)
                .getResultList();
    }

    /**
     * get Debt with Notify by Type
     *
     * @param debtId
     * @return
     */
    public NotifyDebt getDebtsNotifyDebtsByType(int debtId, TypeNotification typeNotification) {
        return em.createNamedQuery("Notify.getDebtsNotifyDebtsByType", NotifyDebt.class)
                .setParameter("debtId", debtId)
                .setParameter("typeNotif", typeNotification)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    public List<NotifyDebt> getNotifyDebtsFromBankAccount(int bankAccId) {
        return em.createNamedQuery("Notify.getNotifyDebtsFromBankAccount", NotifyDebt.class)
                .setParameter("bankAccId", bankAccId)
                .getResultList();
    }

    public NotifyDebt alreadyExistsDebt(int debtId, TypeNotification type) {
        return em.createNamedQuery("Notify.alreadyExists", NotifyDebt.class)
                .setParameter("debtId", debtId)
                .setParameter("type", type)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    public List<NotifyDebt> getNotifyDebtByDebtId(int debtId) {
        return em.createNamedQuery("Notify.getNotifyDebtByDebtId", NotifyDebt.class)
                .setParameter("debtId", debtId)
                .getResultList();
    }

    public void deleteNotifyDebtByDebtId(int debtId) throws Exception {
        try {
            em.createNamedQuery("Notify.deleteNotifyDebtByDebtId")
                    .setParameter("debtId", debtId)
                    .executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception NotifyDebtDao");
        }
    }

    public void deleteNotifyDebtByDebtIdAndType(int debtId, TypeNotification typeNotification) throws Exception {
        try {
            em.createNamedQuery("Notify.deleteNotifyDebtByDebtIdAndType")
                    .setParameter("debtId", debtId)
                    .setParameter("type", typeNotification)
                    .executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception NotifyDebtDao");
        }
    }

    @Override
    public NotifyDebt persist(NotifyDebt entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
        return entity;
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
