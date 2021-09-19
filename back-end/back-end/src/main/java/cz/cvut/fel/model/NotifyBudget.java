package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Table(name = "notifyBudget_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "NotifyBudget.getAll", query = "SELECT n FROM NotifyBudget n"),
        @NamedQuery(name = "NotifyBudget.alreadyExists", query = "SELECT n FROM NotifyBudget n WHERE n.budget.id = :budgetId " +
                "AND n.typeNotification = :type")
})
public class NotifyBudget extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "budget_id")
    @JsonIgnore
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User creator;

    @Enumerated(EnumType.STRING)
    private TypeNotification typeNotification;

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
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
