package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Table(name = "debt_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Debt.getAll", query = "SELECT d FROM Debt d")
})

public class Debt extends AbstractEntity {
    @Column
    private double amount;
    @Column
    private String deadline;
    @Column
    private String name;
    //kazdy mesic, tyden, nebo den...
    @Column
    private String replay;
    @Column
    private String description;
    @Column
    private String notifyDate;

    @ManyToOne
    @JoinColumn(name = "bankAccount_id")
    @JsonIgnore
    private BankAccount bankAccount;

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double castka) {
        this.amount = castka;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String endDate) {
        this.deadline = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReplay() {
        return replay;
    }

    public void setReplay(String replay) {
        this.replay = replay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String popis) {
        this.description = popis;
    }

    public String getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(String startDate) {
        this.notifyDate = startDate;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccountId) {
        this.bankAccount = bankAccountId;
    }
}
