package ru.nchernetsov.controller.cdi;

import ru.nchernetsov.controller.AbstractController;
import ru.nchernetsov.dao.cdi.ProductDAO;
import ru.nchernetsov.entity.cdi.Product;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Optional;

@Named(value = "productEditControllerCDI")
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
        return "/faces/cdi/products";
    }
}
