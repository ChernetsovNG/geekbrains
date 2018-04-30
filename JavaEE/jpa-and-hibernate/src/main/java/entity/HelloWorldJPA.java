package entity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class HelloWorldJPA {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("HelloWorldPU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        Message message = new Message();
        message.setText("Hello World!");
        em.persist(message);
        tx.commit();

        tx.begin();
        List<Message> messages = em.createQuery("SELECT m FROM Message m", Message.class).getResultList();
        System.out.println(messages);
        messages.get(0).setText("Take me to your leader!");
        tx.commit();

        em.close();
        emf.close();
    }
}
