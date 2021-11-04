package cz.cvut.fel.dao;

import cz.cvut.fel.model.Category;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class CategoryDao extends AbstractDao<Category> {
    CategoryDao(EntityManager em) {
        super(em);
    }

    @Override
    public Category find(int id) {
        return em.find(Category.class, id);
    }

    @Override
    public List<Category> findAll() {
        return em.createNamedQuery("Category.getAll", Category.class).getResultList();
    }

    /**
     * Vrati default Categories
     *
     * @return
     */
    public List<Category> getDefaultCategories() {
        return em.createNamedQuery("Category.getDefault", Category.class).getResultList();
    }

    /**
     * Get All users category include default and created categories
     *
     * @param uid
     * @return
     */
    public List<Category> getAllUsersCategory(int uid) {
        try {
            return em.createNativeQuery("SELECT id, name FROM category_table as cat inner JOIN relation_category_user as relation " +
                            "ON relation.category_id = cat.id " +
                            "where relation.user_id = :userId order by cat.id desc",
                    Category.class)
                    .setParameter("userId", uid)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * vrati vse userem vytvorene categorie - pouziva se protoze jenom je ma prarvo editovat, do defult categoies nema pravo zasahovat
     *
     * @param uid
     * @return
     */
    public List<Category> getUsersCreatedCategory(int uid) {
        try {
            return em.createNativeQuery("SELECT cat.id, cat.name  FROM category_table as cat inner JOIN relation_category_user as relation " +
                            "ON relation.category_id = cat.id " +
                            "where relation.user_id = :userId and relation.category_id >= 0 order by cat.id desc",
                    Category.class)
                    .setParameter("userId", uid)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Vrati category userove category
     *
     * @param uid
     * @param catId
     * @return
     * @throws Exception
     */
    public Category getUsersCategoryById(int uid, int catId) throws Exception {
        try {
            return (Category) em.createNativeQuery("SELECT cat.id, cat.name FROM category_table as cat inner JOIN relation_category_user as relation" +
                            " ON relation.category_id = cat.id " +
                            "where relation.user_id = :uid and relation.category_id = :catId",
                    Category.class)
                    .setParameter("uid", uid)
                    .setParameter("catId", catId)
                    .setMaxResults(1)
                    .getResultList()
                    .stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception CategoryDao");
        }
    }

    /**
     * get Users category By name
     *
     * @param uid
     * @param catName
     * @return
     * @throws Exception
     */
    public Category getUsersCategoryByName(int uid, String catName) throws Exception {
        try {
            return (Category) em.createNativeQuery("SELECT cat.id, cat.name FROM category_table as cat inner JOIN relation_category_user as relation " +
                            "ON relation.category_id = cat.id " +
                            "where relation.user_id = :userId and cat.name = :catName",
                    Category.class)
                    .setParameter("userId", uid)
                    .setParameter("catName", catName)
                    .setMaxResults(1)
                    .getResultList()
                    .stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception CategoryDao");
        }
    }

    /**
     * Odstrani realce mezi Category a User v tabulce relation_category_user
     *
     * @param uid
     * @param catId
     * @throws Exception
     */
    public void deleteUsersRelationCategoryById(int uid, int catId) throws Exception {
        try {
            em.createNativeQuery("DELETE FROM relation_category_user " +
                    "WHERE category_id = :catId and user_id = :uid")
                    .setParameter("uid", uid)
                    .setParameter("catId", catId)
                    .executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception CategoryDao");
        }
    }

    @Override
    public Category persist(Category entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
        return entity;
    }

    @Override
    public Category update(Category entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(Category entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
