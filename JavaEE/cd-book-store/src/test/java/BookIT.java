import entity.Book;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.validation.ConstraintViolationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BookIT {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("h2-eclipselink");
    private EntityManager em;
    private EntityTransaction tx;

    @BeforeClass
    public static void populateDB() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        entityManager.persist(new Book(1000L, "Изучаем Java EE 6", "Лучшая книга о Java EE",
            true, "1234-5678", 450, 49.0f));
        entityManager.persist(new Book(1001L, "Изучаем Java EE 7", "Нет, эта лучшая",
            true, "5678-9012", 550, 53.0f));
        entityManager.persist(new Book(1010L, "Властелин колец", "Одно кольцо для управления всеми остальными",
            false, "9012-3456", 222, 23.0f));
        entityTransaction.commit();
        entityManager.close();
    }

    @Before
    public void initEntityManager() {
        em = emf.createEntityManager();
        tx = em.getTransaction();
    }

    @After
    public void closeEntityManager() {
        if (em != null) {
            em.close();
        }
    }

    @Test
    public void shouldFindjavaee7Book() {
        Book book = em.find(Book.class, 1001L);
        assertEquals("Изучаем Java EE 7", book.getTitle());
    }

    @Test
    public void shouldCreateH2G2Book() {
        Book book = new Book("H2G2", "Автостопом по Галактике", 12.5f, "1-84023-742-2", 354, false);
        tx.begin();
        em.persist(book);
        tx.commit();
        assertNotNull("ID не может быть пустым", book.getId());

        book = em.createNamedQuery("findBookH2G2", Book.class).getSingleResult();
        assertEquals("Автостопом по Галактике", book.getDescription());
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldRaiseConstraintViolationCauseNullTitle() {
        Book book = new Book(null, "Пустое название, ошибка", 12.5f, "1-84023-742-2", 354, false);
        em.persist(book);
    }
}
