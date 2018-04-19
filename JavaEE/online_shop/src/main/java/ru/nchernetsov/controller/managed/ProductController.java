package ru.nchernetsov.controller.managed;

import org.javamoney.moneta.Money;
import ru.nchernetsov.dao.managed.ProductDAO;
import ru.nchernetsov.entity.managed.Category;
import ru.nchernetsov.entity.managed.Product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@ManagedBean(name = "productControllerManaged", eager = true)
@ViewScoped
public class ProductController implements Serializable {

    @Inject
    private ProductDAO productDAO;

    @Inject
    private CategoryController categoryController;

    public Collection<Product> getProducts() {
        return new ArrayList<>(productDAO.getProducts());
    }

    public void addProduct(String categoryName, String productName, Double price, String currencyCode) {
        Optional<Category> categoryOptional = categoryController.getCategoryByName(categoryName);
        categoryOptional.ifPresent(category ->
            productDAO.addProduct(new Product(category, productName, Money.of(price, currencyCode))));
    }

    public void removeProduct(Product product) {
        productDAO.removeProduct(product);
    }
}
