<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" lang="en">
    <title>Каталог</title>
    <link rel="stylesheet" type="text/css" href="style.css"/>
</head>
<body>
<div class="container">
    <header class="slider padding-site">
        <div class="header">
            <nav>
                <ul class="menu">
                    <li><a href="main">Главная</a></li>
                    <li><a href="catalog">Каталог</a></li>
                    <li><a href="cart">Корзина</a></li>
                    <li><a href="order">Оформление заказа</a></li>
                    <li><a href="product">Товар</a></li>
                    <li><a href="about">О компании</a></li>
                </ul>
            </nav>
        </div>
    </header>

    <main>
        <div class="padding-site content">
            <h1>Каталог</h1>
            <%--<table>--%>
            <%--<tbody>--%>
            <%--<tr>--%>
            <%--<th>ID</th>--%>
            <%--<th>Name</th>--%>
            <%--<th>Price</th>--%>
            <%--</tr>--%>
            <%--<c:forEach items="${products}" var="product">--%>
            <%--<tr>--%>
            <%--<td><c:out value="${product.id}"/></td>--%>
            <%--<td><c:out value="${product.name}"/></td>--%>
            <%--<td><c:out value="${product.price}"/></td>--%>
            <%--</tr>--%>
            <%--</c:forEach>--%>
            <%--</tbody>--%>
            <%--</table>--%>
            <jsp:useBean id="productList" class="ru.nchernetsov.entity.ProductList" scope="request"/>
            <c:forEach items="${productList.products}" var="product">
                <p><c:out value="${product.id}"/></p>
            </c:forEach>
        </div>
    </main>

    <footer class="footer padding-site">
        <div class="copyright">
            <p>&copy; Все права защищены</p>
        </div>
    </footer>
</div>
</body>
</html>
