package cz.cvut.fel.dao;

import cz.cvut.fel.model.BankAccount;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class BankAccountDao extends AbstractDao<BankAccount> {

    BankAccountDao(EntityManager em) {
        super(em);
    }

    @Override
    public BankAccount find(int id) {
        return em.find(BankAccount.class, id);
    }

    public List<BankAccount> getByNameAvailableBankAcc(String name, int uid) {
        try {
            return em.createNativeQuery("SELECT acc.name, acc.id, acc.balance, acc.currency, acc.user_id " +
                            "FROM bank_account_table as acc inner JOIN relation_bank_account_user as relation " +
                            "ON relation.bank_account_id = acc.id" +
                            " where relation.user_id = :userId and acc.name = :name ",
                    BankAccount.class)
                    .setParameter("userId", uid)
                    .setParameter("name", name)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public BankAccount getUsersAvailableBankAccountById(int uid, int bankAccId) throws Exception {
        try {
            return (BankAccount) em.createNativeQuery("SELECT acc.name, acc.id, acc.balance, acc.currency, acc.user_id" +
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

    public void deleteRelationBankAcc(int uid, int bankAccId) throws Exception {
        try {
            em.createNativeQuery("DELETE FROM relation_bank_account_user WHERE user_id = :uid and bank_account_id = :bankAccId")
                    .setParameter("uid", uid)
                    .setParameter("bankAccId", bankAccId)
                    .executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception BankAccountDao");
        }
    }

    @Override
    public List<BankAccount> findAll() {
        return em.createNamedQuery("BankAccount.getAll", BankAccount.class).getResultList();
    }

    public List<BankAccount> getByNameCreated(String name, int uid) {
        return em.createNamedQuery("BankAccount.getByNameCreated", BankAccount.class)
                .setParameter("name", name)
                .setParameter("uid", uid)
                .getResultList();
    }

    @Override
    public BankAccount persist(BankAccount entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
        return entity;
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
