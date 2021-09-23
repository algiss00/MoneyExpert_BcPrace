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
    public Category find(String id) {
        return em.find(Category.class, id);
    }

    @Override
    public List<Category> findAll() {
        return em.createNamedQuery("Category.getAll").getResultList();
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
