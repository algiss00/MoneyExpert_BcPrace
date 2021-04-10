package cz.cvut.fel.dao;

import cz.cvut.fel.model.Kategorie;
import cz.cvut.fel.model.Rozpocet;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class KategorieDao extends AbstractDao<Kategorie> {
    KategorieDao(EntityManager em) {
        super(em);
    }

    @Override
    public Kategorie find(int id) {
        return em.find(Kategorie.class, id);
    }

    @Override
    public List<Kategorie> findAll() {
        return em.createNamedQuery("Kategorie.getAll").getResultList();
    }

    @Override
    public void persist(Kategorie entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    @Override
    public Kategorie update(Kategorie entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(Kategorie entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
