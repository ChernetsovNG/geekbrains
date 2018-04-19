package ru.nchernetsov.beans.managed;

import org.javamoney.moneta.Money;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "productList", eager = true)
@SessionScoped
public class ProductList implements Serializable {
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

    public ProductList() {
        System.out.println("Hello");
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }
}
