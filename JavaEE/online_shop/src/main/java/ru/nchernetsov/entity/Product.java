package ru.nchernetsov.entity;

import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

/**
 * Товар
 */
@Entity
@Table(name = "product")
public class Product implements Serializable {
    /**
     * Идентификатор
     */
    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;
    /**
     * Категория
     */
    @NotNull(message = "Категория должна быть задана")
    @ManyToOne(cascade = CascadeType.DETACH)
    private Category category;
    /**
     * Заказы
     */
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "products")
    private Collection<Order> orders;
    /**
     * Название
     */
    @NotNull(message = "Название продукта не может быть пустым")
    @Column
    private String name;
    /**
     * Стоимость
     */
    @NotNull(message = "Стоимость продукта должна быть задана")
    @Column
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

    public void setId(UUID id) {
        this.id = id;
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

    public void setPrice(Money price) {
        this.price = price;
    }

    public Collection<Order> getOrders() {
        return orders;
    }

    public void setOrders(Collection<Order> orders) {
        this.orders = orders;
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
