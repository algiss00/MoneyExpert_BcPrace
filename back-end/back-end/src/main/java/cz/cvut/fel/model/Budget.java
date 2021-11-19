package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@Table(name = "budget_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Budget.getAll", query = "SELECT b FROM Budget b"),
        @NamedQuery(name = "Budget.getByBankAccId", query = "SELECT b FROM Budget b where b.bankAccount.id = :bankAccId" +
                " and b.id = :buId"),
        @NamedQuery(name = "Budget.getByName", query = "SELECT b FROM Budget b where b.bankAccount.id = :bankAccId" +
                " and b.name = :name")
})

public class Budget extends AbstractEntity {
    @Column
    @Min(value = 0, message = "amount should not be less than 0")
    private double amount;
    @Column
    private String name;
    @Column
    @Min(value = 0, message = "percentNotify should not be less than 0")
    @Max(value = 100, message = "percentNotify should not be bigger than 100")
    private int percentNotify;

    @Column
    private double sumAmount;

    @ManyToOne
    @JoinColumn(name = "bankAccount_id")
    @JsonIgnore
    private BankAccount bankAccount;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "relation_budget_category",
            joinColumns = @JoinColumn(name = "budget_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> category;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<NotifyBudget> notifyBudgets;

    @OneToMany(mappedBy = "budget")
    @JsonIgnore
    private List<Transaction> transactions;

    public List<Transaction> getTransactions() {
        if (transactions == null) {
            setTransactions(new ArrayList<>());
        }
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<NotifyBudget> getNotifyBudgets() {
        if (notifyBudgets == null) {
            setNotifyBudgets(new ArrayList<>());
        }
        return notifyBudgets;
    }

    public void setNotifyBudgets(List<NotifyBudget> notifyBudget) {
        this.notifyBudgets = notifyBudget;
    }

    public double getSumAmount() {
        return sumAmount;
    }

    public void setSumAmount(double sunAmount) {
        this.sumAmount = sunAmount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPercentNotify() {
        return percentNotify;
    }

    public void setPercentNotify(int percentNotify) {
        this.percentNotify = percentNotify;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccountId) {
        this.bankAccount = bankAccountId;
    }

    public List<Category> getCategory() {
        if (category == null) {
            setCategory(new ArrayList<>());
        }
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }
}
