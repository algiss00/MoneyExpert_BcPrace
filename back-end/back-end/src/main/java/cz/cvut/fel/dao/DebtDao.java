package cz.cvut.fel.dao;

import cz.cvut.fel.model.Debt;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
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
        return em.createNamedQuery("Debt.getAll", Debt.class).getResultList();
    }

    /**
     * Vrati vse Debt u kterych notifyDebt uz nastal ale deadline jeste ne
     *
     * @return
     */
    public List<Debt> getNotifyDebts() {
        return em.createNamedQuery("Debt.getNotifyDebts", Debt.class).getResultList();
    }

    /**
     * get Debts from BankAccount sorted by Deadline
     *
     * @param bankAccId
     * @return
     */
    public List<Debt> getSortedByDeadlineFromBankAcc(int bankAccId) {
        return em.createNamedQuery("Debt.getSortedByDeadlineFromBankAcc", Debt.class)
                .setParameter("bId", bankAccId)
                .getResultList();
    }

    /**
     * Vrati vse debt u kterych deadline date uz nastal
     *
     * @return
     */
    public List<Debt> getDeadlineDebts() {
        return em.createNamedQuery("Debt.getDeadlineDebts", Debt.class).getResultList();
    }

    /**
     * get debt by name in BankAccount
     *
     * @param bankAccId - bankAccount Id
     * @param debtName
     * @return
     */
    public List<Debt> getByNameFromBankAcc(int bankAccId, String debtName) {
        return em.createNamedQuery("Debt.getByNameFromBankAcc", Debt.class)
                .setParameter("baId", bankAccId)
                .setParameter("debtName", debtName)
                .getResultList();
    }

    /**
     * Get Debt from BankAccount by Id
     *
     * @param debtId
     * @param bankAccId
     * @return
     */
    public Debt getByBankAccId(int debtId, int bankAccId) {
        return em.createNamedQuery("Debt.getByBankAccount", Debt.class)
                .setParameter("bankAccId", bankAccId)
                .setParameter("debtId", debtId)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    @Override
    public Debt persist(Debt entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
        return entity;
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
