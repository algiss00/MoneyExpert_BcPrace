package cz.cvut.fel.dao;

import cz.cvut.fel.model.Rozpocet;
import cz.cvut.fel.model.Transakce;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class RozpocetDao extends AbstractDao<Rozpocet>{
    RozpocetDao(EntityManager em) {
        super(em);
    }

    @Override
    public Rozpocet find(int id) {
        return em.find(Rozpocet.class, id);
    }

    @Override
    public List<Rozpocet> findAll() {
        return em.createNamedQuery("Rozpocet.getAll").getResultList();
    }

    @Override
    public void persist(Rozpocet entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    @Override
    public Rozpocet update(Rozpocet entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(Rozpocet entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
