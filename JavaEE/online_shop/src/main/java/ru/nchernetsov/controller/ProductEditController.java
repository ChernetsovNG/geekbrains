package ru.nchernetsov.controller;

import ru.nchernetsov.dao.ProductDAO;
import ru.nchernetsov.entity.Product;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named(value = "productEditController")
@ViewScoped
public class ProductEditController extends AbstractController implements Serializable {

    private final String id = getParamString("id");

    @Inject
    private ProductDAO productDAO;

    private Product product = null;

    @PostConstruct
    private void init() {
        product = productDAO.getProductById(id);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String save() {
        productDAO.merge(product);
        return "/faces/products";
    }
}
