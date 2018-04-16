package ru.nchernetsov.servlet;

import org.javamoney.moneta.Money;
import ru.nchernetsov.entity.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервлет для каталога товаров
 */
@WebServlet(urlPatterns = {"/catalog"})
public class CatalogServlet extends HttpServlet {

    private List<Product> products = new ArrayList<>();

    // Список из 9 продуктов (в нашем случае - книг)
    {
        products.add(new Product("Библиотека Keras - Инструмент глубокого обучения", Money.of(799.0, "RUR")));
        products.add(new Product("Конкурентное программирование на Scala", Money.of(849.0, "RUR")));
        products.add(new Product("Kotlin в действии", Money.of(899.0, "RUR")));
        products.add(new Product("Java 9. Полный обзор нововведений", Money.of(999.0, "RUR")));
        products.add(new Product("Основы блокчейна", Money.of(599.0, "RUR")));
        products.add(new Product("Глубокое обучение. Цветное издание", Money.of(1899.0, "RUR")));
        products.add(new Product("Изучаем Pandas", Money.of(899.0, "RUR")));
        products.add(new Product("Потоковая обработка данных", Money.of(599.0, "RUR")));
        products.add(new Product("Запускаем Ansible", Money.of(999.0, "RUR")));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // req.setAttribute("products", products);
        req.getRequestDispatcher("catalog.jsp").forward(req, resp);
    }
}
