package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "bankAccount_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "BankAccount.getAll", query = "SELECT c FROM BankAccount c"),
})
//@NamedNativeQuery(name = "BankAccount.deleteBankAccFromUser",
//        query = "delete b1 from bank_account_table as b1 right join relation_bank_account_user as r1 on r1.bank_account_id = b1.id where r1.user_id=?")

public class BankAccount extends AbstractEntity {
    @Column
    private String name;
    @Column
    private String currency;
    @Column
    private double balance;

    @ManyToMany(mappedBy = "availableBankAccounts")
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

}
