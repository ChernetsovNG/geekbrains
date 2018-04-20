package ru.nchernetsov.controller.managed;

import ru.nchernetsov.controller.AbstractController;
import ru.nchernetsov.dao.managed.ProductDAO;
import ru.nchernetsov.entity.managed.Product;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Optional;

@ManagedBean(name = "productEditControllerManaged")
@ViewScoped
public class ProductEditController extends AbstractController implements Serializable {

    private final String id = getParamString("id");

    @Inject
    private ProductDAO productDAO;

    private Product product = null;

    @PostConstruct
    private void init() {
        Optional<Product> productOptional = productDAO.getProductById(id);
        product = productOptional.orElse(null);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String save() {
        productDAO.addProduct(product);
        return "/faces/managed/products";
    }
}
