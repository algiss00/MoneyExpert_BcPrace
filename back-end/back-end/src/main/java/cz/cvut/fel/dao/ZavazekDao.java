package cz.cvut.fel.dao;

import cz.cvut.fel.model.User;
import cz.cvut.fel.model.Zavazek;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class ZavazekDao extends AbstractDao<Zavazek> {

    ZavazekDao(EntityManager em) {
        super(em);
    }

    @Override
    public Zavazek find(int id) {
        return em.find(Zavazek.class, id);
    }

    @Override
    public List<Zavazek> findAll() {
        return em.createNamedQuery("Zavazek.getAll").getResultList();
    }

    @Override
    public void persist(Zavazek entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    @Override
    public Zavazek update(Zavazek entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(Zavazek entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
