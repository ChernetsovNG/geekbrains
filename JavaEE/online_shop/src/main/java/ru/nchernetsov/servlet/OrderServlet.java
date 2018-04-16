package ru.nchernetsov.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.nchernetsov.utils.HttpUtils.HTML_FOLDER;

/**
 * Сервлет для оформления заказа
 */
@WebServlet(urlPatterns = {"/order"})
public class OrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(HTML_FOLDER + "/order.jsp").forward(req, resp);
    }
}
