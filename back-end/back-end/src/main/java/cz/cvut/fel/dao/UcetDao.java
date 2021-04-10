package cz.cvut.fel.dao;

import cz.cvut.fel.model.Ucet;
import cz.cvut.fel.model.Zavazek;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class UcetDao extends AbstractDao<Ucet>{

    UcetDao(EntityManager em) {
        super(em);
    }

    @Override
    public Ucet find(int id) {
        return em.find(Ucet.class, id);
    }

    @Override
    public List<Ucet> findAll() {
        return em.createNamedQuery("Ucet.getAll").getResultList();
    }

    @Override
    public void persist(Ucet entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    @Override
    public Ucet update(Ucet entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(Ucet entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
