package entity;

import converter.ZipcodeConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "USERS")
public class User implements Serializable {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    protected String name;

    @Convert(converter = ZipcodeConverter.class, attributeName = "zipcode")
    protected Address homeAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    protected BillingDetails defaultBilling;

    @OneToMany(mappedBy = "user")
    protected Set<BillingDetails> billingDetails = new HashSet<>();

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BillingDetails getDefaultBilling() {
        return defaultBilling;
    }

    public void setDefaultBilling(BillingDetails defaultBilling) {
        this.defaultBilling = defaultBilling;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }
}
