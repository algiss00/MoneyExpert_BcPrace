package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.dto.TypeNotification;

import javax.persistence.*;

@Table(name = "notifyDebt_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Notify.getAll", query = "SELECT n FROM NotifyDebt n"),
        @NamedQuery(name = "Notify.alreadyExists", query = "SELECT n FROM NotifyDebt n WHERE n.debt.id = :debtId" +
                " AND n.typeNotification = :type"),
        @NamedQuery(name = "Notify.getUsersNotifyDebtsByType", query = "SELECT n FROM NotifyDebt n where n.creator.id = :uid " +
                "and n.typeNotification = :typeNotif"),
        @NamedQuery(name = "Notify.getUsersNotifyDebts", query = "SELECT n FROM NotifyDebt n where n.creator.id = :uid"),
        @NamedQuery(name = "Notify.getNotifyDebtByDebtId", query = "SELECT n FROM NotifyDebt n " +
                "where n.creator.id = :uid and n.debt.id = :debtId"),
        @NamedQuery(name = "Notify.deleteNotifyDebtByDebtId", query = "delete FROM NotifyDebt n " +
                "where n.creator.id = :uid and n.debt.id = :debtId")
})
public class NotifyDebt extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "debt_id")
    private Debt debt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User creator;

    @Enumerated(EnumType.STRING)
    private TypeNotification typeNotification;

    public Debt getDebt() {
        return debt;
    }

    public void setDebt(Debt debt) {
        this.debt = debt;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public TypeNotification getTypeNotification() {
        return typeNotification;
    }

    public void setTypeNotification(TypeNotification typeNotification) {
        this.typeNotification = typeNotification;
    }
}
