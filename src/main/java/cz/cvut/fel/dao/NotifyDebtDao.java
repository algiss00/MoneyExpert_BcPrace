package cz.cvut.fel.dao;

import cz.cvut.fel.model.NotifyDebt;
import cz.cvut.fel.dto.TypeNotification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
     * return NotifyDebt for certain Debt id by Notification Type
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

    /**
     * get all NotifyDebts from BankAccount
     *
     * @param bankAccId
     * @return
     */
    public List<NotifyDebt> getNotifyDebtsFromBankAccount(int bankAccId) {
        return em.createNamedQuery("Notify.getNotifyDebtsFromBankAccount", NotifyDebt.class)
                .setParameter("bankAccId", bankAccId)
                .getResultList();
    }

    /**
     * check if NotifyDebt already exists for debt with a certain type.
     *
     * @param debtId
     * @param type
     * @return
     */
    public NotifyDebt alreadyExistsDebt(int debtId, TypeNotification type) {
        return em.createNamedQuery("Notify.alreadyExists", NotifyDebt.class)
                .setParameter("debtId", debtId)
                .setParameter("type", type)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    /**
     * get NotifyDebts by certain Debt.
     *
     * @param debtId
     * @return
     */
    public List<NotifyDebt> getNotifyDebtByDebtId(int debtId) {
        return em.createNamedQuery("Notify.getNotifyDebtByDebtId", NotifyDebt.class)
                .setParameter("debtId", debtId)
                .getResultList();
    }

    /**
     * remove all NotifyDebts, which have certain debt id.
     *
     * @param debtId
     * @throws Exception
     */
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

    /**
     * Delete NotifyDebt, which have certain debt id and type
     *
     * @param debtId
     * @param typeNotification
     * @throws Exception
     */
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
