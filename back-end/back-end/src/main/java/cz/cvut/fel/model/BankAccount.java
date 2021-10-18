package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.dto.TypeCurrency;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "bankAccount_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "BankAccount.getAll", query = "SELECT b FROM BankAccount b")
})
public class BankAccount extends AbstractEntity {
    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    private TypeCurrency currency;

    @Column
    private double balance;

    @ManyToMany(mappedBy = "availableBankAccounts", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<User> owners;

    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "bankAccount")
    @JsonIgnore
    private List<Budget> budgets;

    @OneToMany(mappedBy = "bankAccount")
    @JsonIgnore
    private List<Debt> debts;

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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

}
