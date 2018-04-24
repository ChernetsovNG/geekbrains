package ru.nchernetsov.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import static ru.nchernetsov.utils.TimeUtils.unixTimeToUTC;

/**
 * Заказ
 */
@Entity
@Table(name = "orders")
public class Order implements Serializable {
    /**
     * Идентификатор
     */
    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;
    /**
     * UNIX-time создания заказа
     */
    @Column
    private Long utc;
    /**
     * Список товаров в заказе
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(name = "order_product",
        joinColumns = {@JoinColumn(name = "order_id", nullable = false, updatable = false)},
        inverseJoinColumns = {@JoinColumn(name = "product_id", nullable = false, updatable = false)})
    @Column
    private Collection<Product> products;

    public Order() {
        this.id = UUID.randomUUID();
        this.utc = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getUtc() {
        return utc;
    }

    public void setUtc(Long utc) {
        this.utc = utc;
    }

    public LocalDateTime getUtcLocalDateTime() {
        return unixTimeToUTC(utc);
    }

    public Collection<Product> getProducts() {
        return products;
    }

    public void setProducts(Collection<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;

        Order order = (Order) o;

        if (!getUtc().equals(order.getUtc())) return false;
        if (getId() != null ? !getId().equals(order.getId()) : order.getId() != null) return false;
        return getProducts() != null ? getProducts().equals(order.getProducts()) : order.getProducts() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (int) (getUtc() ^ (getUtc() >>> 32));
        result = 31 * result + (getProducts() != null ? getProducts().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
            "id=" + id +
            ", utc=" + getUtcLocalDateTime() +
            ", products=" + products +
            '}';
    }
}
