package cz.cvut.fel.dao;

import cz.cvut.fel.model.NotifyDebt;
import cz.cvut.fel.dto.TypeNotification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
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

    public List<NotifyDebt> getUsersNotifyDebts(int uid) {
        return em.createNamedQuery("Notify.getUsersNotifyDebts", NotifyDebt.class)
                .setParameter("uid", uid)
                .getResultList();
    }

    public List<NotifyDebt> getUsersNotifyDebtsByType(int uid, TypeNotification typeNotification) {
        return em.createNamedQuery("Notify.getUsersNotifyDebtsByType", NotifyDebt.class)
                .setParameter("uid", uid)
                .setParameter("typeNotif", typeNotification)
                .getResultList();
    }

    public NotifyDebt alreadyExistsDebt(int debtId, TypeNotification type) throws Exception {
        try {
            return em.createNamedQuery("Notify.alreadyExists", NotifyDebt.class)
                    .setParameter("debtId", debtId)
                    .setParameter("type", type)
                    .setMaxResults(1)
                    .getResultList()
                    .stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception NotifyDebtDao");
        }
    }

    public List<NotifyDebt> getNotifyDebtByDebtId(int uid, int debtId) throws Exception {
        try {
            return em.createNamedQuery("Notify.getNotifyDebtByDebtId", NotifyDebt.class)
                    .setParameter("uid", uid)
                    .setParameter("debtId", debtId)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void deleteNotifyDebtByDebtId(int uid, int debtId) throws Exception {
        try {
            em.createNamedQuery("Notify.deleteNotifyDebtByDebtId")
                    .setParameter("uid", uid)
                    .setParameter("debtId", debtId)
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
