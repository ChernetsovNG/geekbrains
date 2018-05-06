package ru.nchernetsov.service.rest;

import ru.nchernetsov.controller.ProductController;
import ru.nchernetsov.entity.Product;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("products")
public class ProductRS {
    @Inject
    private ProductController productController;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @WebMethod
    public void addProduct(Product product) {
        productController.addProduct(product);
    }

    @DELETE
    @Path("/{id}")
    @WebMethod
    public void removeProduct(@PathParam("id") String id) {
        productController.removeProduct(id);
    }

    @GET
    @Path("/{id}")
    @WebMethod
    @Produces({MediaType.APPLICATION_JSON})
    public Product getProductById(@PathParam("id") String id) {
        return productController.getProductById(id);
    }

    @GET
    @Path("/names/{name}")
    @WebMethod
    @Produces({MediaType.APPLICATION_JSON})
    public Product getProductByName(@PathParam("name") String name) {
        return productController.getProductByName(name);
    }

    @GET
    @WebMethod
    @Produces({MediaType.APPLICATION_JSON})
    public Collection<Product> getAllProducts() {
        return productController.getProducts();
    }

    @GET
    @Path("/categories/{categoryId}")
    @WebMethod
    @Produces({MediaType.APPLICATION_JSON})
    public Collection<Product> getProductsByCategoryId(@PathParam("categoryId") String categoryId) {
        return productController.getProductsByCategoryId(categoryId);
    }
}
