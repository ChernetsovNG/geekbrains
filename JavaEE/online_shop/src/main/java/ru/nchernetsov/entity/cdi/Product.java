package ru.nchernetsov.entity.cdi;

import org.javamoney.moneta.Money;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
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
    @NotNull(message = "Категория должна быть задана")
    private Category category;
    /**
     * Название
     */
    @NotNull(message = "Название продукта не может быть пустым")
    private String name;
    /**
     * Стоимость
     */
    @NotNull(message = "Стоимость продукта должна быть задана")
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

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        String[] currencyCodeAmount = price.split(" ");
        String currencyCode = currencyCodeAmount[0];
        Double amount = Double.parseDouble(currencyCodeAmount[1]);
        this.price = Money.of(amount, currencyCode);
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
