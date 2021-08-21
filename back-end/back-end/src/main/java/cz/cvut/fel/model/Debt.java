package cz.cvut.fel.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "debt_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Debt.getAll", query = "SELECT d FROM Debt d"),
        @NamedQuery(
                name = "Category.getAllAccountsDebts",
                query = "SELECT d FROM Debt d WHERE d.bankAccount.id = :accId"),
        @NamedQuery(
                name = "Category.getAllUsersDebts",
                query = "SELECT d FROM Debt d WHERE d.creator.id = :uid")
})

public class Debt extends AbstractEntity {
    @Column
    private double amount;
    @Column
    private String endDate;
    @Column
    private String name;
    @Column
    private String replay;
    @Column
    private String description;
    @Column
    private String startDate;

    @ManyToOne
    @JoinColumn(name = "bankAccount_id")
    private BankAccount bankAccount;

    @ManyToOne
    @JoinColumn(name = "user_id")
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

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccountId) {
        this.bankAccount = bankAccountId;
    }
}
