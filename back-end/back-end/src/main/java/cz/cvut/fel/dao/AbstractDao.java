package cz.cvut.fel.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public abstract class AbstractDao<T> {
    @PersistenceContext
    protected EntityManager em;

    AbstractDao(EntityManager em) {
        this.em = em;
    }

    abstract public T find(int id);

    abstract public List<T> findAll();

    abstract public T persist(T entity);

    abstract public T update(T entity);

    abstract public void remove(T entity);
}
