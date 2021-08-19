package cz.cvut.fel.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "category_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Category.getAll", query = "SELECT c FROM Category c"),
        //todo
//        @NamedQuery(
//                name = "Category.getAllUsersCategories",
//                query = "SELECT c FROM Category c WHERE c.creator.id = :uid")
})
public class Category extends AbstractEntity {
    @Column
    private String name;

    @OneToOne(mappedBy = "category")
    @JsonIgnore
    private Budget budget;

    @ManyToMany(mappedBy = "myCategories")
    @JsonIgnore
    private List<User> creator;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Transaction> transactions;

    //todo get only transactions for auth user
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

    public List<User> getCreator() {
        return creator;
    }

    public void setCreator(List<User> creator) {
        this.creator = creator;
    }
}
