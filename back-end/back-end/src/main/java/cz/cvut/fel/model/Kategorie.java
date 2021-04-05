package cz.cvut.fel.model;

import javax.persistence.*;
import java.util.List;

@Table(name = "kategorie_table")
@Entity
@NamedQuery(name = "Kategorie.getAll", query = "SELECT c FROM kategorie c")
public class Kategorie extends AbstractEntity {
    @Column
    private String name;
    @OneToOne(mappedBy = "kategorieId")
    private Rozpocet rozpocetId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creatorId;

    @OneToMany(mappedBy = "kategorieId")
    private List<Transakce> transakces;

    public List<Transakce> getTransakces() {
        return transakces;
    }

    public void setTransakces(List<Transakce> transakces) {
        this.transakces = transakces;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rozpocet getRozpocetId() {
        return rozpocetId;
    }

    public void setRozpocetId(Rozpocet rozpocetId) {
        this.rozpocetId = rozpocetId;
    }

    public User getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(User creatorId) {
        this.creatorId = creatorId;
    }
}
