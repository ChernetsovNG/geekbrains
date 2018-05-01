import entity.Item;
import entity.MonetaryAmount;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;

public class TablesTest {
    @Test
    public void tablesTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Postgres");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();

        Item item = new Item();
        item.setName("Item 1");
        item.setAuctionEnd(ZonedDateTime.of(LocalDateTime.of(2018, Month.MAY, 10, 12, 0, 0),
            ZoneId.of("Europe/Moscow")));
        item.setBuyNowPrice(new MonetaryAmount(new BigDecimal(25.0), Currency.getInstance("USD")));
        item.setInitialPrice(new MonetaryAmount(new BigDecimal(15.0), Currency.getInstance("EUR")));
        em.persist(item);

        List<Item> items = em.createQuery("SELECT i FROM Item i WHERE i.name LIKE :name", Item.class)
            .setParameter("name", "Item 1")
            .getResultList();

        System.out.println(items);

        tx.commit();

        em.close();
        emf.close();
    }
}
