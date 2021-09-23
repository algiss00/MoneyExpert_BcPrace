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

    abstract public T find(String id);

    abstract public List<T> findAll();

    abstract public void persist(T entity);

    abstract public T update(T entity);

    abstract public void remove(T entity);
}
