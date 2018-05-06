package ru.nchernetsov.entity;

import ru.nchernetsov.interceptor.LoggerInterceptor;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Корзина с покупками
 */
@Stateful
@Interceptors({LoggerInterceptor.class})
public class ShoppingCart {
    private UUID id;
    private List<Product> contents;

    @PostConstruct
    public void initialize() {
        id = UUID.randomUUID();
        contents = new ArrayList<>();
    }

    public void addProduct(Product product) {
        if (!contents.contains(product)) {
            contents.add(product);
        }
    }

    public void removeProduct(Product product) {
        contents.remove(product);
    }

    public List<Product> getContents() {
        return contents;
    }

    public void remove() {
        contents.clear();
        contents = null;
    }

    @Override
    public String toString() {
        return "CartBean{" +
            "id=" + id +
            ", contents=" + contents +
            '}';
    }
}
