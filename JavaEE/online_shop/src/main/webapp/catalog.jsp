<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8" lang="en">
	<title>Каталог</title>
	<link rel="stylesheet" type="text/css" href="style.css"/>
	<script defer src="https://use.fontawesome.com/releases/v5.0.9/js/all.js"
	        integrity="sha384-8iPTk2s/jMVj81dnzb/iFR2sdA7u06vHJyyLlAd4snFpCl/SnyUjRrbdJsw1pGIl"
	        crossorigin="anonymous"></script>
</head>
<body>
<div class="container">
	<header class="slider padding-site">
		<div class="header">
			<%@ include file="fragments/menu.jsp" %>
		</div>
	</header>
	
	<main>
		<div class="padding-site content">
			<%--<jsp:useBean id="productList" class="ru.nchernetsov.entity.ProductList" scope="request"/>--%>
			<h1>Каталог</h1>
			<table>
				<tbody>
				<tr>
					<th>ID</th>
					<th>Name</th>
					<th>Price</th>
				</tr>
				<c:forEach items="${requestScope.products}" var="product">
					<tr>
						<td><c:out value="${product.id}"/></td>
						<td><c:out value="${product.name}"/></td>
						<td><c:out value="${product.price}"/></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</div>
	</main>
	
	<footer class="footer padding-site">
		<%@ include file="fragments/copyright.jsp" %>
	</footer>
</div>
</body>
</html>
