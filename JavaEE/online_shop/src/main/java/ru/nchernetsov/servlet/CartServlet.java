package ru.nchernetsov.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Сервлет для корзины
 */
@WebServlet(urlPatterns = {"/cart"})
@ServletSecurity(
    @HttpConstraint(rolesAllowed = {"admin", "user"},
        transportGuarantee = ServletSecurity.TransportGuarantee.NONE)
)
public class CartServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("cart.jsp").forward(req, resp);
    }
}
