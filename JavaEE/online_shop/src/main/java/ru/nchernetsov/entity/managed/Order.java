package ru.nchernetsov.entity.managed;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import static ru.nchernetsov.utils.TimeUtils.unixTimeToUTC;

/**
 * Заказ
 */
@ManagedBean(name = "orderManaged", eager = true)
@SessionScoped
public class Order {
    /**
     * Идентификатор
     */
    private UUID id;
    /**
     * UNIX-time создания заказа
     */
    private long utc;
    /**
     * Список товаров в заказе
     */
    private Collection<Product> products;

    public Order() {
    }

    public Order(Collection<Product> products) {
        this.id = UUID.randomUUID();
        this.utc = System.currentTimeMillis();
        this.products = products;
    }

    public UUID getId() {
        return id;
    }

    public long getUtc() {
        return utc;
    }

    public LocalDateTime getUtcLocalDateTime() {
        return unixTimeToUTC(utc);
    }

    public Collection<Product> getProducts() {
        return products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;

        Order order = (Order) o;

        if (getUtc() != order.getUtc()) return false;
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
