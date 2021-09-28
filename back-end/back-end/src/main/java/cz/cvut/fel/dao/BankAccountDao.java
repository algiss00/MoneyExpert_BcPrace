package cz.cvut.fel.dao;

import cz.cvut.fel.model.BankAccount;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class BankAccountDao extends AbstractDao<BankAccount> {

    BankAccountDao(EntityManager em) {
        super(em);
    }

    @Override
    public BankAccount find(int id) {
        return em.find(BankAccount.class, id);
    }

    public List<BankAccount> getByName(String name, int uid) throws Exception {
        try {
            return em.createNativeQuery("SELECT acc.name, acc.id, acc.balance, acc.currency " +
                            "FROM bank_account_table as acc inner JOIN relation_bank_account_user as relation " +
                            "ON relation.bank_account_id = acc.id" +
                            " where relation.user_id = :userId and acc.name = :name ",
                    BankAccount.class)
                    .setParameter("userId", uid)
                    .setParameter("name", name)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception BankAccountDao");
        }
    }

    public BankAccount getUsersBankAccountById(int uid, int bankAccId) throws Exception {
        try {
            return (BankAccount) em.createNativeQuery("SELECT acc.name, acc.id, acc.balance, acc.currency" +
                            " FROM bank_account_table as acc inner JOIN relation_bank_account_user as relation " +
                            "ON relation.bank_account_id = acc.id " +
                            "where relation.user_id = :userId and relation.bank_account_id = :bankAccId",
                    BankAccount.class)
                    .setParameter("userId", uid)
                    .setParameter("bankAccId", bankAccId)
                    .setMaxResults(1)
                    .getResultList()
                    .stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception BankAccountDao");
        }
    }

    @Override
    public List<BankAccount> findAll() {
        return em.createNamedQuery("BankAccount.getAll", BankAccount.class).getResultList();
    }

    @Override
    public void persist(BankAccount entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    @Override
    public BankAccount update(BankAccount entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(BankAccount entity) {
        Objects.requireNonNull(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
