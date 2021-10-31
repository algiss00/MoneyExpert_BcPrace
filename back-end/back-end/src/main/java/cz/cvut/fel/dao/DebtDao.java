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

    public List<Debt> getNotifyDebts() {
        return em.createNamedQuery("Debt.getNotifyDebts", Debt.class).getResultList();
    }

    public List<Debt> getSortedByDeadlineFromBankAcc(int bId) {
        return em.createNamedQuery("Debt.getSortedByDeadlineFromBankAcc", Debt.class)
                .setParameter("bId", bId)
                .getResultList();
    }

    public List<Debt> getDeadlineDebts() {
        return em.createNamedQuery("Debt.getDeadlineDebts", Debt.class).getResultList();
    }

    public List<Debt> getByNameFromBankAcc(int baId, String debtName) {
        try {
            return em.createNamedQuery("Debt.getByNameFromBankAcc", Debt.class)
                    .setParameter("baId", baId)
                    .setParameter("debtName", debtName)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Debt getByBankAccId(int debtId, int bankAccId) throws Exception {
        try {
            return em.createNamedQuery("Debt.getByBankAccount", Debt.class)
                    .setParameter("bankAccId", bankAccId)
                    .setParameter("debtId", debtId)
                    .setMaxResults(1)
                    .getResultList()
                    .stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception DebtDao");
        }
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
