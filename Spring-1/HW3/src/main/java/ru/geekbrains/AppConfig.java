package ru.geekbrains;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScan("ru.geekbrains")
public class AppConfig {

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/geekbrains-spring-1");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        // Создание класса фабрики, реализующей интерфейс FactoryBean<EntityManagerFactory>
        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        // Задание источника подключения
        factory.setDataSource(dataSource());
        // Задание адаптера для конкретной реализации JPA
        // указывает, какая именно библиотека будет использоваться в качестве persistence provider
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        // Указание пакетов, в которых будут находиться классы-сущности
        factory.setPackagesToScan("ru.geekbrains.entity");
        factory.setPersistenceUnitName("persistenceUnit");
        // Создание свойств для настройки Hibernate
        final Properties properties = new Properties();
        // Указание диалекта конкретной базы данных – необходимо для генерации запросов Hibernate к БД
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
        // Указание максимальной глубины связи
        properties.put("hibernate.max_fetch_depth", 3);
        // Определение максимального количества строк, возвращаемых за один запрос из БД
        properties.put("hibernate.jdbc.fetch_size", 50);
        // Определение максимального количества запросов при использовании пакетных операций
        properties.put("hibernate.jdbc.batch_size", 10);
        // Включает логирование
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        factory.setJpaProperties(properties);
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
        return transactionManager;
    }

}
