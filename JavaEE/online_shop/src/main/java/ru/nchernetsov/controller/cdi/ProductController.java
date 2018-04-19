package ru.nchernetsov.controller.cdi;

import org.javamoney.moneta.Money;
import ru.nchernetsov.dao.cdi.ProductDAO;
import ru.nchernetsov.entity.cdi.Category;
import ru.nchernetsov.entity.cdi.Product;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Named(value = "productControllerCDI")
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
        // Находим по имени соответствующую категорию, и, если она существует, добавляем продукт
        Optional<Category> categoryOptional = categoryController.getCategoryByName(categoryName);
        categoryOptional.ifPresent(category ->
            productDAO.addProduct(new Product(category, productName, Money.of(price, currencyCode))));
    }

    public void removeProduct(Product product) {
        productDAO.removeProduct(product);
    }
}
