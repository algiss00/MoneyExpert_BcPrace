package cz.cvut.fel.dao;

import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.Category;
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

    public Budget getByCategory(int categoryId, int bankAccId) throws Exception {
        try {
            return em.createNamedQuery("Budget.getBudgetByCategory", Budget.class)
                    .setParameter("categoryId", categoryId)
                    .setParameter("bankAccId", bankAccId)
                    .setMaxResults(1)
                    .getResultList()
                    .stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception BudgetDao");
        }
    }

    public Budget getByBankAcc(int buId, int bankAccId) throws Exception {
        try {
            return em.createNamedQuery("Budget.getByBankAccId", Budget.class)
                    .setParameter("buId", buId)
                    .setParameter("bankAccId", bankAccId)
                    .setMaxResults(1)
                    .getResultList()
                    .stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception BudgetDao");
        }
    }

    public List<Budget> getByName(int bankAccId, String name) {
        return em.createNamedQuery("Budget.getByName", Budget.class)
                .setParameter("bankAccId", bankAccId)
                .setParameter("name", name)
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
