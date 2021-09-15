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
                query = "SELECT d FROM Debt d WHERE d.deadline <= CURRENT_DATE")
})
//@NamedNativeQuery(name = "getNotifyDebts", query = "SELECT * FROM debt_table WHERE CAST(notify_date as Date) <= CURRENT_DATE AND CAST(deadline as Date) > CURRENT_DATE")

public class Debt extends AbstractEntity {
    @Column
    private double amount;
    @Column
    private Date deadline;
    @Column
    private String name;
    //kazdy mesic, tyden, nebo den...
    @Enumerated(EnumType.STRING)
    private TypeReplay replay;
    @Column
    private String description;
    @Column
    private Date notifyDate;

    @ManyToOne
    @JoinColumn(name = "bankAccount_id")
    @JsonIgnore
    private BankAccount bankAccount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User creator;

    @OneToMany(mappedBy = "debt")
    @JsonIgnore
    private List<Notify> notify;

    public List<Notify> getNotify() {
        if (notify == null) {
            setNotify(new ArrayList<>());
        }
        return notify;
    }

    public void setNotify(List<Notify> notify) {
        this.notify = notify;
    }

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

    public TypeReplay getReplay() {
        return replay;
    }

    public void setReplay(TypeReplay replay) {
        this.replay = replay;
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
