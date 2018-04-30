package entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.StringTokenizer;

@Entity
public class User implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    protected String firstname;
    protected String lastname;

    public Long getId() {
        return id;
    }

    public String getName() {
        return firstname + " " + lastname;
    }

    public void setName(String name) {
        StringTokenizer t = new StringTokenizer(name);
        firstname = t.nextToken();
        lastname = t.nextToken();
    }
}
