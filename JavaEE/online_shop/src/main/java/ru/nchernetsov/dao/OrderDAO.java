package ru.nchernetsov.dao;

import ru.nchernetsov.entity.Order;

import javax.ejb.Stateless;
import javax.inject.Named;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Named(value = "orderDAO")
@Stateless
public class OrderDAO extends AbstractDAO {

    public Collection<Order> getOrders() {
        List list = em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
        return list;
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
