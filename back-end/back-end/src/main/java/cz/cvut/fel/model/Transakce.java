package cz.cvut.fel.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "transakce_table")
@Entity
@NamedQuery(name = "Transakce.getAll", query = "SELECT c FROM Transakce c")
public class Transakce extends AbstractEntity{
    @Column
    private double castka;
    @Column
    private Date datum;
    @Column
    private String pozanamky;
    @Enumerated(EnumType.STRING)
    private TypTransakce typTransakce;

    @ManyToOne
    @JoinColumn(name = "kategorie_id")
    private Kategorie kategorieId;
    @ManyToOne
    @JoinColumn(name = "ucet_id")
    private Ucet ucetId;

    public String getPozanamky() {
        return pozanamky;
    }

    public void setPozanamky(String pozanamky) {
        this.pozanamky = pozanamky;
    }

    public double getCastka() {
        return castka;
    }

    public void setCastka(double castka) {
        this.castka = castka;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public TypTransakce getTypTransakce() {
        return typTransakce;
    }

    public void setTypTransakce(TypTransakce typTransakce) {
        this.typTransakce = typTransakce;
    }

    public Kategorie getKategorieId() {
        return kategorieId;
    }

    public void setKategorieId(Kategorie kategorieId) {
        this.kategorieId = kategorieId;
    }

    public Ucet getUcetId() {
        return ucetId;
    }

    public void setUcetId(Ucet ucetId) {
        this.ucetId = ucetId;
    }
}
