package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.dto.TypeCurrency;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "bankAccount_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "BankAccount.getAll", query = "SELECT b FROM BankAccount b"),
        @NamedQuery(name = "BankAccount.getByNameCreated", query = "SELECT b FROM BankAccount b " +
                "where b.name = :name and b.creator.id = :uid")
})
public class BankAccount extends AbstractEntity {
    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    private TypeCurrency currency;

    @Column
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User creator;

    @ManyToMany(mappedBy = "availableBankAccounts")
    @JsonIgnore
    private List<User> owners;

    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Budget> budgets;

    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Debt> debts;

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<User> getOwners() {
        if (owners == null) {
            setOwners(new ArrayList<>());
        }
        return owners;
    }

    public void setOwners(List<User> owners) {
        this.owners = owners;
    }

    public List<Transaction> getTransactions() {
        if (transactions == null) {
            setTransactions(new ArrayList<>());
        }
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Budget> getBudgets() {
        if (budgets == null) {
            setBudgets(new ArrayList<>());
        }
        return budgets;
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
    }

    public List<Debt> getDebts() {
        if (debts == null) {
            setDebts(new ArrayList<>());
        }
        return debts;
    }

    public void setDebts(List<Debt> debts) {
        this.debts = debts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(TypeCurrency currency) {
        this.currency = currency;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

}
