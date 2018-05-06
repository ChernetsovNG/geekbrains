package inheritance;

import entity.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Currency;

public class PolymorphicManyToOne {

    private EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction tx;

    @Before
    public void beforeEachTest() {
        emf = Persistence.createEntityManagerFactory("Postgres");
        em = emf.createEntityManager();
        tx = em.getTransaction();
    }

    @After
    public void afterEachTest() {
        em.close();
        emf.close();
    }

    @Test
    public void test1() {
        CreditCard cc = new CreditCard("John Doe", "1234123412341234", "06", "2015");

        User johnDoe = new User("John Doe");
        johnDoe.setDefaultBilling(cc);
        johnDoe.setHomeAddress(new Address("Lebedeva", "164502", "Severodvinsk"));
        cc.setUser(johnDoe);

        tx.begin();
        em.persist(cc);
        em.persist(johnDoe);
        tx.commit();
    }

    @Test
    public void test2() {
        Item someItem = new Item("Some item");
        someItem.setInitialPrice(new MonetaryAmount(new BigDecimal(10.0), Currency.getInstance("USD")));
        someItem.setBuyNowPrice(new MonetaryAmount(new BigDecimal(15.0), Currency.getInstance("USD")));

        Bid someBid = new Bid(new BigDecimal("123.00"), someItem);
        someItem.getBids().add(someBid);

        tx.begin();
        em.persist(someItem);
        em.persist(someBid);
        tx.commit();
    }
}
