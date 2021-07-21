package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "category_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Category.getAll", query = "SELECT c FROM Category c"),
        @NamedQuery(
                name = "Category.getAllUsersCategories",
                query = "SELECT c FROM Category c WHERE c.creator.id = :uid")
})

public class Category extends AbstractEntity {
    @Column
    private String name;

    @OneToOne(mappedBy = "category")
    @JsonIgnore
    private Budget budget;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User creator;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Transaction> transactions;

    public List<Transaction> getTransactions() {
        if (transactions == null) {
            setTransactions(new ArrayList<>());
        }
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
}
