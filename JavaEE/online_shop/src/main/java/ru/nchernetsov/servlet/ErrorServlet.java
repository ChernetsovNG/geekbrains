package ru.nchernetsov.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.nchernetsov.utils.HttpUtils.HTML_FOLDER;

/**
 * Обработка ошибок 403, 404
 */
@WebServlet(urlPatterns = {"/error403", "/error404"})
public class ErrorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (resp.getStatus()) {
            case 403:
                req.getRequestDispatcher(HTML_FOLDER + "/error/error403.html").forward(req, resp);
                break;
            case 404:
                req.getRequestDispatcher(HTML_FOLDER + "/error/error404.html").forward(req, resp);
                break;
        }
    }
}
