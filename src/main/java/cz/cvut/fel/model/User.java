package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    @Column(unique = true)
    private String username;
    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<BankAccount> createdBankAccounts;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "relation_bankAccount_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "bankAccount_id"))
    @JsonIgnore
    private List<BankAccount> availableBankAccounts;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "relation_category_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @JsonIgnore
    private List<Category> myCategories;

    public List<BankAccount> getCreatedBankAccounts() {
        if (createdBankAccounts == null) {
            setCreatedBankAccounts(new ArrayList<>());
        }
        return createdBankAccounts;
    }

    public void setCreatedBankAccounts(List<BankAccount> createdBankAccounts) {
        this.createdBankAccounts = createdBankAccounts;
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

    public List<BankAccount> getAvailableBankAccounts() {
        if (availableBankAccounts == null) {
            setAvailableBankAccounts(new ArrayList<>());
        }
        return availableBankAccounts;
    }

    public void setAvailableBankAccounts(List<BankAccount> availableBankAccounts) {
        this.availableBankAccounts = availableBankAccounts;
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
