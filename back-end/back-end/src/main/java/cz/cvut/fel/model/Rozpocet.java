package cz.cvut.fel.model;

import javax.persistence.*;

@Table(name = "rozpocet_table")
@Entity
@NamedQuery(name = "Rozpocet.getAll", query = "SELECT c FROM Rozpocet c")
public class Rozpocet extends AbstractEntity {
    @Column
    private double castka;
    @Column
    private String name;
    @Column
    private int procentUpozorneni;

    @ManyToOne
    @JoinColumn(name = "ucet_id")
    private Ucet ucetId;
    @OneToOne
    @JoinColumn(name = "kategorie_id")
    private Kategorie kategorieId;


    public double getCastka() {
        return castka;
    }

    public void setCastka(double castka) {
        this.castka = castka;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProcentUpozorneni() {
        return procentUpozorneni;
    }

    public void setProcentUpozorneni(int procentUpozorneni) {
        this.procentUpozorneni = procentUpozorneni;
    }

    public Ucet getUcetId() {
        return ucetId;
    }

    public void setUcetId(Ucet ucetId) {
        this.ucetId = ucetId;
    }

    public Kategorie getKategorieId() {
        return kategorieId;
    }

    public void setKategorieId(Kategorie kategorieId) {
        this.kategorieId = kategorieId;
    }
}
