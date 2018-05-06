package ru.nchernetsov.service.web;

import ru.nchernetsov.controller.ProductController;
import ru.nchernetsov.entity.Product;

import javax.inject.Inject;
import javax.jws.WebService;
import java.util.Collection;

@WebService(endpointInterface = "ru.nchernetsov.service.web.IProduct")
public class ProductWS implements IProduct {

    @Inject
    private ProductController productController;

    @Override
    public void addProduct(Product product) {
        productController.addProduct(product);
    }

    @Override
    public void removeProduct(Product product) {
        productController.removeProduct(product);
    }

    @Override
    public Product getProductById(String id) {
        return productController.getProductById(id);
    }

    @Override
    public Product getProductByName(String name) {
        return productController.getProductByName(name);
    }

    @Override
    public Collection<Product> getAllProducts() {
        return productController.getProducts();
    }

    @Override
    public Collection<Product> getProductsByCategoryId(String categoryId) {
        return productController.getProductsByCategoryId(categoryId);
    }
}
