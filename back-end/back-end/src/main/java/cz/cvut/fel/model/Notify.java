package cz.cvut.fel.model;

import javax.persistence.*;

@Table(name = "notify_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Notify.getAll", query = "SELECT n FROM Notify n")
})
public class Notify extends AbstractEntity {
    @OneToOne
    @JoinColumn(name = "debt_id")
    private Debt debt;

    @OneToOne
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
