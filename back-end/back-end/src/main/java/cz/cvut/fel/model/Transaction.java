package cz.cvut.fel.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "transaction_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Transaction.getAll", query = "SELECT c FROM Transaction c"),
        @NamedQuery(
                name = "Transaction.getAllFromAccount",
                query = "SELECT c FROM Transaction c WHERE c.bankAccount.id = :id"),
        @NamedQuery(
                name = "Transaction.getAllFromCategory",
                query = "SELECT c FROM Transaction c WHERE c.category.id = :catId AND c.bankAccount.id = :accId")
})

public class Transaction extends AbstractEntity {
    @Column
    private double amount;
    @Column
    private String date;
    @Column
    private String jottings;
    @Enumerated(EnumType.STRING)
    private TypeTransaction typeTransaction;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "bankAccount_id")
    private BankAccount bankAccount;

    public String getJottings() {
        return jottings;
    }

    public void setJottings(String pozanamky) {
        this.jottings = pozanamky;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double castka) {
        this.amount = castka;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String datum) {
        this.date = datum;
    }

    public TypeTransaction getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(TypeTransaction typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }
}
