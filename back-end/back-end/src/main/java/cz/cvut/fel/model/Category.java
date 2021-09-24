package cz.cvut.fel.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "category_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "Category.getAll", query = "SELECT c FROM Category c"),
        // todo get by name and by creator
        // @NamedQuery(name = "Category.getByName", query = "SELECT c FROM Category c where c.name = :name")
})
public class Category extends AbstractEntity {
    @Column
    private String name;

    @OneToOne(mappedBy = "category")
    @JsonIgnore
    private Budget budget;

    @ManyToMany(mappedBy = "myCategories")
    @JsonIgnore
    private List<User> creators;

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

    public List<User> getCreators() {
        if (creators == null) {
            setCreators(new ArrayList<>());
        }
        return creators;
    }

    public void setCreators(List<User> creators) {
        this.creators = creators;
    }
}
