package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Table(name = "notifyDebt_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Notify.getAll", query = "SELECT n FROM NotifyDebt n"),
        @NamedQuery(name = "Notify.alreadyExists", query = "SELECT n FROM NotifyDebt n WHERE n.debt.id = :debtId AND n.typeNotification = :type")
})
public class NotifyDebt extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "debt_id")
    @JsonIgnore
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
