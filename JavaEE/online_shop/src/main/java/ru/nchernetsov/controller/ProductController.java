package ru.nchernetsov.controller;

import org.javamoney.moneta.Money;
import ru.nchernetsov.dao.OrderDAO;
import ru.nchernetsov.dao.ProductDAO;
import ru.nchernetsov.entity.ShoppingCart;
import ru.nchernetsov.entity.Category;
import ru.nchernetsov.entity.Order;
import ru.nchernetsov.entity.Product;
import ru.nchernetsov.interceptor.LoggerInterceptor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.Collection;

@Named(value = "productController")
@ApplicationScoped
@Interceptors({LoggerInterceptor.class})
public class ProductController {

    @Inject
    private ProductDAO productDAO;

    @Inject
    private OrderDAO orderDAO;

    @Inject
    private CategoryController categoryController;

    @Inject
    private ShoppingCart shoppingCart;

    public Collection<Product> getProducts() {
        return new ArrayList<>(productDAO.getProducts());
    }

    public void addProduct(String categoryName, String productName, String price, String currencyCode) {
        // Находим по имени соответствующую категорию, и, если она существует, добавляем продукт
        Category category = categoryController.getCategoryByName(categoryName);
        if (category != null) {
            productDAO.persist(new Product(category, productName, Money.of(Double.parseDouble(price), currencyCode)));
        }
    }

    public void addProductToOrder(Product product, String orderId) {
        Order order = orderDAO.getOrderById(orderId);

        order.getProducts().add(product);
        product.getOrders().add(order);

        productDAO.merge(product);
        orderDAO.merge(order);
    }

    public void addProductToCart(Product product) {
        shoppingCart.addProduct(product);
    }

    public Collection<Product> getCartProducts() {
        return shoppingCart.getContents();
    }

    public void removeProductFromCart(Product product) {
        shoppingCart.removeProduct(product);
    }

    public void removeProduct(Product product) {
        productDAO.removeProduct(product.getId());
    }
}
