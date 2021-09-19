package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Table(name = "transaction_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Transaction.getAll", query = "SELECT t FROM Transaction t"),
        @NamedQuery(
                name = "Transaction.getAllFromCategory",
                query = "SELECT t FROM Transaction t WHERE t.category.id = :catId AND t.bankAccount.id = :accId"),
        @NamedQuery(
                name = "Transaction.getFromBankAccount",
                query = "SELECT t FROM Transaction t WHERE t.bankAccount.id = :bankAccId and t.id = :transId")
})

public class Transaction extends AbstractEntity {
    @Column
    private double amount;
    @Column
    private Date date;
    @Column
    private String jottings;
    @Enumerated(EnumType.STRING)
    private TypeTransaction typeTransaction;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bankAccount_id")
    @JsonIgnore
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TypeTransaction getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(TypeTransaction typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
