package cz.cvut.fel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Table(name = "user_table")
@Entity
@NamedQuery(name = "User.getAll", query = "SELECT c FROM User c")
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
    @JsonIgnore
    private String password;

    @OneToOne(mappedBy = "creatorId")
    private Ucet myUcet;

    @ManyToMany
    @JoinTable(
            name = "relation_ucet_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "ucet_id"))
    private List<Ucet> availableUcty;

    @OneToMany(mappedBy = "creatorId")
    private List<Kategorie> myCategories;

    public User() {
    }

    public Ucet getMyUcet() {
        return myUcet;
    }

    public void setMyUcet(Ucet myUcet) {
        this.myUcet = myUcet;
    }

    public List<Ucet> getAvailableUcty() {
        return availableUcty;
    }

    public void setAvailableUcty(List<Ucet> dostupneUcty) {
        this.availableUcty = dostupneUcty;
    }

    public List<Kategorie> getMyCategories() {
        return myCategories;
    }

    public void setMyCategories(List<Kategorie> myKategories) {
        this.myCategories = myKategories;
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
