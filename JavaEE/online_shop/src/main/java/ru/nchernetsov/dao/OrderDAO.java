package ru.nchernetsov.dao;

import ru.nchernetsov.entity.Order;
import ru.nchernetsov.interceptor.LoggerInterceptor;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.Collection;
import java.util.UUID;

@Stateless
@Interceptors({LoggerInterceptor.class})
public class OrderDAO extends AbstractDAO {

    public Collection<Order> getOrders() {
        return em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
    }

    public Order getOrderById(String id) {
        if (id == null) {
            return null;
        }
        return em.find(Order.class, UUID.fromString(id));
    }

    public void persist(Order order) {
        if (order == null) {
            return;
        }
        em.persist(order);
    }

    public void merge(Order order) {
        if (order == null) {
            return;
        }
        em.merge(order);
    }

    public void removeOrder(Order order) {
        if (order == null) {
            return;
        }
        em.remove(order);
    }

    public void removeOrder(UUID orderId) {
        if (orderId == null) {
            return;
        }
        Order order = em.find(Order.class, orderId);
        em.remove(order);
    }
}
