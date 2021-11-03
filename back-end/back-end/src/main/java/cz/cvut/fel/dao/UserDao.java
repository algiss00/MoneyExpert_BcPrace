package cz.cvut.fel.dao;

import cz.cvut.fel.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class UserDao extends AbstractDao<User> {

    UserDao(EntityManager em) {
        super(em);
    }

    @Override
    public User find(int id) {
        return em.find(User.class, id);
    }

    @Override
    public List<User> findAll() {
        return em.createNamedQuery("User.getAll", User.class).getResultList();
    }

    public User getByUsername(String username) throws UsernameNotFoundException {
        return em.createNamedQuery("User.getByUsername", User.class)
                .setParameter("name", username)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    public User getByEmail(String email) {
        return em.createNamedQuery("User.getByEmail", User.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    @Override
    public User persist(User entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
        return entity;
    }

    @Override
    public User update(User entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(User entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
