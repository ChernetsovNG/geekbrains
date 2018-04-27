package ru.nchernetsov.dao;

import ru.nchernetsov.entity.Product;
import ru.nchernetsov.interceptor.LoggerInterceptor;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.Collection;
import java.util.UUID;

@Stateless
@Interceptors({LoggerInterceptor.class})
public class ProductDAO extends AbstractDAO {

    public Collection<Product> getProducts() {
        return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    public Product getProductById(String id) {
        if (id == null) {
            return null;
        }
        return em.find(Product.class, UUID.fromString(id));
    }

    public void persist(Product product) {
        if (product == null) {
            return;
        }
        em.persist(product);
    }

    public void merge(Product product) {
        if (product == null) {
            return;
        }
        em.merge(product);
    }

    public void removeProduct(Product product) {
        if (product == null) {
            return;
        }
        em.remove(product);
    }

    public void removeProduct(UUID productId) {
        if (productId == null) {
            return;
        }
        Product product = em.find(Product.class, productId);
        em.remove(product);
    }
}
