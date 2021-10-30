package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Table(name = "budget_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Budget.getAll", query = "SELECT b FROM Budget b"),
        @NamedQuery(name = "Budget.getBudgetByCategory", query = "SELECT b FROM Budget b where b.category.id = :categoryId" +
                " and b.bankAccount.id = :bankAccId"),
        @NamedQuery(name = "Budget.getByBankAccId", query = "SELECT b FROM Budget b where b.bankAccount.id = :bankAccId" +
                " and b.id = :buId"),
        @NamedQuery(name = "Budget.getByName", query = "SELECT b FROM Budget b where b.bankAccount.id = :bankAccId" +
                " and b.name = :name")
})

public class Budget extends AbstractEntity {
    @Column
    private double amount;
    @Column
    private String name;
    @Column
    private int percentNotif;

    @Column
    private double sumAmount;
    
    @ManyToOne
    @JoinColumn(name = "bankAccount_id")
    @JsonIgnore
    private BankAccount bankAccount;

    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User creator;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<NotifyBudget> notifyBudgets;

    public List<NotifyBudget> getNotifyBudgets() {
        return notifyBudgets;
    }

    public void setNotifyBudgets(List<NotifyBudget> notifyBudget) {
        this.notifyBudgets = notifyBudget;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
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

    public void setAmount(double castka) {
        this.amount = castka;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPercentNotif() {
        return percentNotif;
    }

    public void setPercentNotif(int percentNotif) {
        this.percentNotif = percentNotif;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccountId) {
        this.bankAccount = bankAccountId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
