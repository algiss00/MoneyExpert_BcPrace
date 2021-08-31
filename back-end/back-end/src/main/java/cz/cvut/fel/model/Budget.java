package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Table(name = "budget_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Budget.getAll", query = "SELECT b FROM Budget b")
})

public class Budget extends AbstractEntity {
    @Column
    private double amount;
    @Column
    private String name;
    @Column
    private int percentNotif;

    private double sumAmount;

    //todo meli jsme tu cascade.Persist ale proc? bank acc uz treba exituje, nechci aby se vytvarel novy BankAcc
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

    public void setPercentNotif(int procentUpozorneni) {
        this.percentNotif = procentUpozorneni;
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
