package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name = "debt_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Debt.getAll", query = "SELECT d FROM Debt d"),
        @NamedQuery(name = "Debt.getNotifyDebts",
                query = "SELECT d FROM Debt d WHERE d.notifyDate <= CURRENT_DATE AND d.deadline > CURRENT_DATE"),
        @NamedQuery(name = "Debt.getDeadlineDebts",
                query = "SELECT d FROM Debt d WHERE d.deadline <= CURRENT_DATE"),
        @NamedQuery(name = "Debt.getByNameFromBankAcc",
                query = "SELECT d FROM Debt d WHERE d.bankAccount.id = :baId and d.name = :debtName"),
        @NamedQuery(name = "Debt.getByBankAccount",
                query = "SELECT d FROM Debt d WHERE d.bankAccount.id = :bankAccId and d.id = :debtId"),
        @NamedQuery(name = "Debt.getSortedByDeadlineFromBankAcc",
                query = "SELECT d FROM Debt d WHERE d.bankAccount.id = :bId order by d.deadline ASC")
})
public class Debt extends AbstractEntity {
    @Column
    private double amount;
    @Column
    private Date deadline;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private Date notifyDate;

    @ManyToOne
    @JoinColumn(name = "bankAccount_id")
    @JsonIgnore
    private BankAccount bankAccount;

    @OneToMany(mappedBy = "debt", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<NotifyDebt> notifyDebt;

    public List<NotifyDebt> getNotifyDebt() {
        if (notifyDebt == null) {
            setNotifyDebt(new ArrayList<>());
        }
        return notifyDebt;
    }

    public void setNotifyDebt(List<NotifyDebt> notifyDebt) {
        this.notifyDebt = notifyDebt;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double castka) {
        this.amount = castka;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date endDate) {
        this.deadline = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String popis) {
        this.description = popis;
    }

    public Date getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(Date startDate) {
        this.notifyDate = startDate;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccountId) {
        this.bankAccount = bankAccountId;
    }
}
