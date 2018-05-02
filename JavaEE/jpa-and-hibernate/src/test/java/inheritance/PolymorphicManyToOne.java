package inheritance;

import entity.Address;
import entity.CreditCard;
import entity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

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
}
