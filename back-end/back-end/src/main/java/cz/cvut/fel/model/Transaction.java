package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.dto.TypeTransaction;

import javax.validation.constraints.Min;
import javax.persistence.*;
import java.util.Date;

@Table(name = "transaction_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Transaction.getAll", query = "SELECT t FROM Transaction t order by t.date desc"),
        @NamedQuery(
                name = "Transaction.getFromBankAccount",
                query = "SELECT t FROM Transaction t WHERE t.bankAccount.id = :bankAccId and t.id = :transId"),
        @NamedQuery(
                name = "Transaction.getAllFromBankAccount",
                query = "SELECT t FROM Transaction t WHERE t.bankAccount.id = :bankAccId order by t.date desc"),
        @NamedQuery(
                name = "Transaction.getByTransactionType",
                query = "SELECT t FROM Transaction t WHERE t.bankAccount.id = :bankAccId and t.typeTransaction = :type order by t.date desc"),
        @NamedQuery(
                name = "Transaction.getAllTransactionsByCategory",
                query = "SELECT t FROM Transaction t WHERE t.bankAccount.id = :bankAccId and t.category.id = :categoryId order by t.date desc"),
        @NamedQuery(
                name = "Transaction.getAllTransactionsByCategoryAndType",
                query = "SELECT t FROM Transaction t WHERE t.bankAccount.id = :bankAccId " +
                        "and t.category.id = :categoryId and t.typeTransaction = :type order by t.date desc")
})

public class Transaction extends AbstractEntity {
    @Column
    @Min(value = 0, message = "amount should not be less than 0")
    private double amount;
    @Column
    private Date date;
    @Column
    private String jottings;
    @Enumerated(EnumType.STRING)
    private TypeTransaction typeTransaction;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    @JsonIgnore
    private Budget budget;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bankAccount_id")
    @JsonIgnore
    private BankAccount bankAccount;

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public String getJottings() {
        return jottings;
    }

    public void setJottings(String jottings) {
        this.jottings = jottings;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
