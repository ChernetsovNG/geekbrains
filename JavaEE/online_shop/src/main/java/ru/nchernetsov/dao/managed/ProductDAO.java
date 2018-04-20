package ru.nchernetsov.dao.managed;

import org.javamoney.moneta.Money;
import ru.nchernetsov.entity.managed.Category;
import ru.nchernetsov.entity.managed.Product;

import javax.faces.bean.ManagedBean;
import javax.inject.Singleton;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ManagedBean(name = "productDAOManaged")
@Singleton
public class ProductDAO implements Serializable {
    private final List<Product> products = new ArrayList<>();

    {
        Category booksCategory = new Category("books");

        products.add(new Product(booksCategory, "Библиотека Keras - Инструмент глубокого обучения", Money.of(799.0, "RUR")));
        products.add(new Product(booksCategory, "Конкурентное программирование на Scala", Money.of(849.0, "RUR")));
        products.add(new Product(booksCategory, "Kotlin в действии", Money.of(899.0, "RUR")));
        products.add(new Product(booksCategory, "Java 9. Полный обзор нововведений", Money.of(999.0, "RUR")));
        products.add(new Product(booksCategory, "Основы блокчейна", Money.of(599.0, "RUR")));
        products.add(new Product(booksCategory, "Глубокое обучение. Цветное издание", Money.of(1899.0, "RUR")));
        products.add(new Product(booksCategory, "Изучаем Pandas", Money.of(899.0, "RUR")));
        products.add(new Product(booksCategory, "Потоковая обработка данных", Money.of(599.0, "RUR")));
        products.add(new Product(booksCategory, "Запускаем Ansible", Money.of(999.0, "RUR")));
    }

    public ProductDAO() {
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
