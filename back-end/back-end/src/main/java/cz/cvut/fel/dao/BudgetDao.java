package cz.cvut.fel.dao;

import cz.cvut.fel.model.BankAccount;
import cz.cvut.fel.model.Budget;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class BudgetDao extends AbstractDao<Budget> {
    BudgetDao(EntityManager em) {
        super(em);
    }

    @Override
    public Budget find(int id) {
        return em.find(Budget.class, id);
    }

    @Override
    public List<Budget> findAll() {
        return em.createNamedQuery("Budget.getAll").getResultList();
    }

    public List<Budget> getAllAccountsBudgets(int accId) {
        return em.createNamedQuery("Budget.getAllAccountBudgets", Budget.class)
                .setParameter("bankAccId", accId)
                .getResultList();
    }

    public List<Budget> getAllUsersBudgets(int uid) {
        return em.createNamedQuery("Budget.getAllUsersBudgets", Budget.class)
                .setParameter("userId", uid)
                .getResultList();
    }

    @Override
    public void persist(Budget entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    @Override
    public Budget update(Budget entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(Budget entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
