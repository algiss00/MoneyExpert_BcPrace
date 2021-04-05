package cz.cvut.fel.model;

import javax.persistence.*;
import java.util.List;

@Table(name = "ucet_table")
@Entity
@NamedQuery(name = "Ucet.getAll", query = "SELECT c FROM ucet c")
public class Ucet extends AbstractEntity {
    @Column
    private String name;
    @Column
    private String typMeny;
    @Column
    private double zustatek;

    @ManyToMany(mappedBy = "dostupneUcty")
    private List<User> vlastniky;

    @OneToMany(mappedBy = "ucetId")
    private List<Transakce> transakces;

    @OneToMany(mappedBy = "ucetId")
    private List<Rozpocet> rozpocety;

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

    public List<Transakce> getTransakces() {
        return transakces;
    }

    public void setTransakces(List<Transakce> transakces) {
        this.transakces = transakces;
    }

    public List<Rozpocet> getRozpocety() {
        return rozpocety;
    }

    public void setRozpocety(List<Rozpocet> rozpocety) {
        this.rozpocety = rozpocety;
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
