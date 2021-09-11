package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "user_table")
@Entity
@NamedQueries({
        @NamedQuery(name = "User.getAll", query = "SELECT u FROM User u"),
        @NamedQuery(
                name = "User.getByUsername",
                query = "SELECT u FROM User u WHERE u.username = :name"),
        @NamedQuery(
                name = "User.getByEmail",
                query = "SELECT u FROM User u WHERE u.email = :email")
})
public class User extends AbstractEntity {
    @Column
    private String email;
    @Column
    private String name;
    @Column
    private String lastname;
    @Column
    private String username;
    @Column
    private String password;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "relation_bankAccount_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "bankAccount_id"))
    @JsonIgnore
    private List<BankAccount> availableBankAccounts;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "relation_category_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @JsonIgnore
    private List<Category> myCategories;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Budget> myBudgets;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Debt> myDebts;

    @OneToOne(mappedBy = "creator")
    @JsonIgnore
    private Notify notify;

    public Notify getNotify() {
        return notify;
    }

    public void setNotify(Notify notify) {
        this.notify = notify;
    }

    public List<Category> getMyCategories() {
        if (myCategories == null) {
            setMyCategories(new ArrayList<>());
        }
        return myCategories;
    }

    public void setMyCategories(List<Category> myCategories) {
        this.myCategories = myCategories;
    }

    public List<Budget> getMyBudgets() {
        if (myBudgets == null) {
            setMyBudgets(new ArrayList<>());
        }
        return myBudgets;
    }

    public void setMyBudgets(List<Budget> myBudgets) {
        this.myBudgets = myBudgets;
    }

    public List<Debt> getMyDebts() {
        if (myDebts == null) {
            setMyDebts(new ArrayList<>());
        }
        return myDebts;
    }

    public void setMyDebts(List<Debt> myDebts) {
        this.myDebts = myDebts;
    }

    public List<BankAccount> getAvailableBankAccounts() {
        if (availableBankAccounts == null) {
            setAvailableBankAccounts(new ArrayList<>());
        }
        return availableBankAccounts;
    }

    public void setAvailableBankAccounts(List<BankAccount> dostupneUcty) {
        this.availableBankAccounts = dostupneUcty;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof User) {
            User user = (User) obj;
            return getUsername().equals(user.getUsername());
        }
        return false;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
