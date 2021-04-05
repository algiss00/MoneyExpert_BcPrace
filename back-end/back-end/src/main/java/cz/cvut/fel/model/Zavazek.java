package cz.cvut.fel.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "zavazek_table")
@Entity
@NamedQuery(name = "Zavazek.getAll", query = "SELECT c FROM zavazek c")
public class Zavazek extends AbstractEntity {
    @Column
    private double castka;
    @Column
    private Date endDate;
    @Column
    private String name;
    @Column
    private String opakovani;
    @Column
    private String popis;
    @Column
    private Date startDate;

    @ManyToOne
    @JoinColumn(name = "ucet_id")
    private Ucet ucetId;

    public double getCastka() {
        return castka;
    }

    public void setCastka(double castka) {
        this.castka = castka;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpakovani() {
        return opakovani;
    }

    public void setOpakovani(String opakovani) {
        this.opakovani = opakovani;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Ucet getUcetId() {
        return ucetId;
    }

    public void setUcetId(Ucet ucetId) {
        this.ucetId = ucetId;
    }
}
