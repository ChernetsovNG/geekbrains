package ru.nchernetsov.dao.cdi;

import org.javamoney.moneta.Money;
import ru.nchernetsov.entity.cdi.Product;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Named(value = "productDAOCDI")
@ApplicationScoped
public class ProductDAO {
    private final List<Product> products = new ArrayList<>();

    @Inject
    private CategoryDAO categoryDAO;

    public ProductDAO() {
    }

    @PostConstruct
    public void createProducts() {
        products.add(new Product(categoryDAO.getCategoryByName("books").orElse(null),
            "Библиотека Keras - Инструмент глубокого обучения", Money.of(799.0, "RUR")));
        products.add(new Product(categoryDAO.getCategoryByName("books").orElse(null),
            "Конкурентное программирование на Scala", Money.of(849.0, "RUR")));
        products.add(new Product(categoryDAO.getCategoryByName("books").orElse(null),
            "Kotlin в действии", Money.of(899.0, "RUR")));
        products.add(new Product(categoryDAO.getCategoryByName("books").orElse(null),
            "Java 9. Полный обзор нововведений", Money.of(999.0, "RUR")));
        products.add(new Product(categoryDAO.getCategoryByName("books").orElse(null),
            "Основы блокчейна", Money.of(599.0, "RUR")));
        products.add(new Product(categoryDAO.getCategoryByName("books").orElse(null),
            "Глубокое обучение. Цветное издание", Money.of(1899.0, "RUR")));
        products.add(new Product(categoryDAO.getCategoryByName("books").orElse(null),
            "Изучаем Pandas", Money.of(899.0, "RUR")));
        products.add(new Product(categoryDAO.getCategoryByName("books").orElse(null),
            "Потоковая обработка данных", Money.of(599.0, "RUR")));
        products.add(new Product(categoryDAO.getCategoryByName("books").orElse(null),
            "Запускаем Ansible", Money.of(999.0, "RUR")));
    }

    public Collection<Product> getProducts() {
        return products;
    }

    public Optional<Product> getProductById(String id) {
        return products.stream()
            .filter(product -> product.getId().toString().equals(id))
            .findFirst();
    }

    public void addProduct(Product product) {
        if (!products.contains(product)) {
            products.add(product);
        }
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }
}
