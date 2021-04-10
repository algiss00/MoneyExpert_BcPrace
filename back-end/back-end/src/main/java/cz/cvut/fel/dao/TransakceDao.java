package cz.cvut.fel.dao;

import cz.cvut.fel.model.Transakce;
import cz.cvut.fel.model.Ucet;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class TransakceDao extends AbstractDao<Transakce> {
    TransakceDao(EntityManager em) {
        super(em);
    }

    @Override
    public Transakce find(int id) {
        return em.find(Transakce.class, id);
    }

    @Override
    public List<Transakce> findAll() {
        return em.createNamedQuery("Transakce.getAll").getResultList();
    }

    @Override
    public void persist(Transakce entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    @Override
    public Transakce update(Transakce entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(Transakce entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
