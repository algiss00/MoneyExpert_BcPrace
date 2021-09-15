package cz.cvut.fel.model;

import javax.persistence.*;

@Table(name = "notify_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Notify.getAll", query = "SELECT n FROM Notify n"),
        @NamedQuery(name = "Notify.alreadyExists", query = "SELECT n FROM Notify n WHERE n.debt.id = :debtId AND n.typeNotification = :type")
})
public class Notify extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "debt_id")
    private Debt debt;

    @ManyToOne
    @JoinColumn(name = "user_id")
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
