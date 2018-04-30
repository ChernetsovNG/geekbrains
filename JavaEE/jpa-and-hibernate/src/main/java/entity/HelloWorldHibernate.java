package entity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.EntityTransaction;
import java.util.List;

public class HelloWorldHibernate {
    public static void main(String[] args) {
        StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();

        serviceRegistryBuilder
            .applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
            .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
            .applySetting("hibernate.connection.url", "jdbc:h2:mem:h2")
            .applySetting("hibernate.connection.user", "sa")
            .applySetting("hibernate.hbm2ddl.auto", "create-drop")
            .applySetting("hibernate.show_sql", "true")
            .applySetting("hibernate.format_sql", "true")
            .applySetting("hibernate.use_sql_comments", "true");

        ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();

        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        metadataSources.addAnnotatedClass(Message.class);
        MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
        Metadata metadata = metadataBuilder.build();

        SessionFactory sessionFactory = metadata.buildSessionFactory();
        Session session = sessionFactory.getCurrentSession();
        EntityTransaction tx = session.getTransaction();

        tx.begin();
        Message message = new Message();
        message.setText("Hello World!");
        session.persist(message);
        tx.commit();

        tx.begin();
        List messages = sessionFactory.getCurrentSession().createCriteria(Message.class).list();
        System.out.println(messages);
        tx.commit();

        session.close();
        sessionFactory.close();
    }
}


