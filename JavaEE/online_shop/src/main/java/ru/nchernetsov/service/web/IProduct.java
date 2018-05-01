package ru.nchernetsov.service.web;

import ru.nchernetsov.entity.Product;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.Collection;

@WebService
public interface IProduct {
    @WebMethod
    void addProduct(Product product);

    @WebMethod
    void removeProduct(Product product);

    @WebMethod
    Product getProductById(String id);

    @WebMethod
    Product getProductByName(String name);

    @WebMethod
    Collection<Product> getAllProducts();

    @WebMethod
    Collection<Product> getProductsByCategoryId(String categoryId);
}
