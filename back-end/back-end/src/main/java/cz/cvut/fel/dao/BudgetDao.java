package cz.cvut.fel.dao;

import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.Category;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
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
        return em.createNamedQuery("Budget.getAll", Budget.class).getResultList();
    }

    /**
     * Vrati budget podle category v urcitem BankAccount
     * v jednom bankAccount je pouze jeden budget na urcitou Category, tj. nejsou dva budgety na stejnou Category v jednom BankAcc
     *
     * @param categoryId
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public Budget getByCategory(int categoryId, int bankAccId) throws Exception {
        try {
            return (Budget) em.createNativeQuery("SELECT bud.id, bud.amount, bud.name, bud.percent_notify, bud.sum_amount,  bud.bank_account_id " +
                            "FROM budget_table as bud inner JOIN relation_budget_category as relation" +
                            " ON relation.budget_id = bud.id " +
                            "where relation.category_id = :catId and bud.bank_account_id = :bankAccId",
                    Budget.class)
                    .setParameter("catId", categoryId)
                    .setParameter("bankAccId", bankAccId)
                    .setMaxResults(1)
                    .getResultList()
                    .stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception BudgetDao");
        }
    }

    /**
     * get Budget z urciteho BankAccount
     *
     * @param budgetId  - budget Id
     * @param bankAccId
     * @return
     * @throws Exception
     */
    public Budget getByBankAcc(int budgetId, int bankAccId) {
        return em.createNamedQuery("Budget.getByBankAccId", Budget.class)
                .setParameter("buId", budgetId)
                .setParameter("bankAccId", bankAccId)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    /**
     * get Budget by name from BankAccount
     *
     * @param bankAccId
     * @param name
     * @return
     */
    public List<Budget> getByName(int bankAccId, String name) {
        return em.createNamedQuery("Budget.getByName", Budget.class)
                .setParameter("bankAccId", bankAccId)
                .setParameter("name", name)
                .getResultList();
    }

    /**
     * Odstrani vsechny realce mezi budget a category v tabulce relation_budget_category
     *
     * @param budgetId
     * @throws Exception
     */
    public void deleteAllBudgetRelationWithCategoryById(int budgetId) throws Exception {
        try {
            em.createNativeQuery("DELETE FROM relation_budget_category " +
                    "WHERE budget_id = :budgetId")
                    .setParameter("budgetId", budgetId)
                    .executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception BudgetDao");
        }
    }

    @Override
    public Budget persist(Budget entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
        return entity;
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
