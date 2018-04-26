package ru.nchernetsov.controller;

import ru.nchernetsov.dao.OrderDAO;
import ru.nchernetsov.entity.Order;
import ru.nchernetsov.interceptor.LoggerInterceptor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.Collection;

@Named(value = "orderController")
@ApplicationScoped
@Interceptors({LoggerInterceptor.class})
public class OrderController {

    @Inject
    private OrderDAO orderDAO;

    public Collection<Order> getOrders() {
        return new ArrayList<>(orderDAO.getOrders());
    }

    public void addOrder() {
        orderDAO.persist(new Order());
    }

    public void removeOrder(Order order) {
        orderDAO.removeOrder(order.getId());
    }
}
