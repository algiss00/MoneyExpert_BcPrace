package cz.cvut.fel.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class AbstractEntity implements Serializable {
    @Id
    @GeneratedValue
    private int id;

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
