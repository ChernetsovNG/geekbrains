package ru.nchernetsov.controller;

import ru.nchernetsov.dao.OrderDAO;
import ru.nchernetsov.entity.Order;
import ru.nchernetsov.interceptor.LoggerInterceptor;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptors;
import java.io.Serializable;

@Named(value = "orderEditController")
@ViewScoped
@Interceptors({LoggerInterceptor.class})
public class OrderEditController extends AbstractController implements Serializable {

    private final String id = getParamString("id");

    @Inject
    private OrderDAO orderDAO;

    private Order order = null;

    @PostConstruct
    private void init() {
        order = orderDAO.getOrderById(id);
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String save() {
        orderDAO.merge(order);
        return "/faces/orders";
    }
}
