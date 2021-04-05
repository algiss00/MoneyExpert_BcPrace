package cz.cvut.fel.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@MappedSuperclass
public class AbstractEntity implements Serializable {
    @Transient
    private Map<String, String> url;
    @Id
    @GeneratedValue
    private int id;

    AbstractEntity() {
        url = new HashMap<String, String>();
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUrl(Map<String, String> url) {
        this.url = url;
    }

    public Map<String, String> getUrl() {
        return url;
    }
}
