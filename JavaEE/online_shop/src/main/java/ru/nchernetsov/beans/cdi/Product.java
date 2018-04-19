package ru.nchernetsov.beans.cdi;

import org.javamoney.moneta.Money;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.UUID;

/**
 * Товар
 */
@Named(value = "productCDI")
@SessionScoped
public class Product implements Serializable {
    /**
     * Идентификатор
     */
    private UUID id;
    /**
     * Категория
     */
    private Category category;
    /**
     * Название
     */
    private String name;
    /**
     * Стоимость
     */
    private Money price;

    public Product() {
    }

    public Product(Category category, String name, Money price) {
        this.id = UUID.randomUUID();
        this.category = category;
        this.name = name;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        if (getId() != null ? !getId().equals(product.getId()) : product.getId() != null) return false;
        if (getCategory() != null ? !getCategory().equals(product.getCategory()) : product.getCategory() != null)
            return false;
        if (getName() != null ? !getName().equals(product.getName()) : product.getName() != null) return false;
        return getPrice() != null ? getPrice().equals(product.getPrice()) : product.getPrice() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getCategory() != null ? getCategory().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPrice() != null ? getPrice().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
            "id=" + id +
            ", category=" + category +
            ", name='" + name + '\'' +
            ", price=" + price +
            '}';
    }
}
