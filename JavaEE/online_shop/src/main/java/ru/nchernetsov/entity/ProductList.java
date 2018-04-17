package ru.nchernetsov.entity;

import org.javamoney.moneta.Money;

import java.util.ArrayList;
import java.util.List;

public class ProductList {
    private List<Product> products = new ArrayList<>();

    public ProductList() {
        products.add(new Product("Библиотека Keras - Инструмент глубокого обучения", Money.of(799.0, "RUR")));
        products.add(new Product("Конкурентное программирование на Scala", Money.of(849.0, "RUR")));
        products.add(new Product("Kotlin в действии", Money.of(899.0, "RUR")));
        products.add(new Product("Java 9. Полный обзор нововведений", Money.of(999.0, "RUR")));
        products.add(new Product("Основы блокчейна", Money.of(599.0, "RUR")));
        products.add(new Product("Глубокое обучение. Цветное издание", Money.of(1899.0, "RUR")));
        products.add(new Product("Изучаем Pandas", Money.of(899.0, "RUR")));
        products.add(new Product("Потоковая обработка данных", Money.of(599.0, "RUR")));
        products.add(new Product("Запускаем Ansible", Money.of(999.0, "RUR")));
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
