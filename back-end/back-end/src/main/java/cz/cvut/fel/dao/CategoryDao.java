package cz.cvut.fel.dao;

import cz.cvut.fel.model.Category;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
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

    public List<Category> getDefaultCategories() {
        return em.createNamedQuery("Category.getDefault", Category.class).getResultList();
    }

    public List<Category> getUsersCategory(int uid) throws Exception {
        try {
            return em.createNativeQuery("SELECT * FROM category_table as cat inner JOIN relation_category_user as relation " +
                            "ON relation.category_id = cat.id " +
                            "where relation.user_id = :userId",
                    Category.class)
                    .setParameter("userId", uid)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception CategoryDao");
        }
    }

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

    @Override
    public void persist(Category entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
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
