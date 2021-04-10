package cz.cvut.fel.model;

import javax.persistence.*;
import java.util.List;

@Table(name = "ucet_table")
@Entity
@NamedQuery(name = "Ucet.getAll", query = "SELECT c FROM Ucet c")
public class Ucet extends AbstractEntity {
    @Column
    private String name;
    @Column
    private String typMeny;
    @Column
    private double zustatek;

    @ManyToMany(mappedBy = "availableUcty")
    private List<User> vlastniky;

    @OneToMany(mappedBy = "ucetId", cascade = CascadeType.ALL)
    private List<Transakce> transakce;

    @OneToMany(mappedBy = "ucetId", cascade = CascadeType.ALL)
    private List<Rozpocet> rozpocty;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User creatorId;

    @OneToMany(mappedBy = "ucetId")
    private List<Zavazek> zavazky;

    public List<User> getVlastniky() {
        return vlastniky;
    }

    public void setVlastniky(List<User> vlastniky) {
        this.vlastniky = vlastniky;
    }

    public List<Transakce> getTransakce() {
        return transakce;
    }

    public void setTransakce(List<Transakce> transakces) {
        this.transakce = transakces;
    }

    public List<Rozpocet> getRozpocty() {
        return rozpocty;
    }

    public void setRozpocty(List<Rozpocet> rozpocety) {
        this.rozpocty = rozpocety;
    }

    public List<Zavazek> getZavazky() {
        return zavazky;
    }

    public void setZavazky(List<Zavazek> zavazky) {
        this.zavazky = zavazky;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypMeny() {
        return typMeny;
    }

    public void setTypMeny(String typMeny) {
        this.typMeny = typMeny;
    }

    public double getZustatek() {
        return zustatek;
    }

    public void setZustatek(double zustatek) {
        this.zustatek = zustatek;
    }

    public User getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(User creatorId) {
        this.creatorId = creatorId;
    }
}
