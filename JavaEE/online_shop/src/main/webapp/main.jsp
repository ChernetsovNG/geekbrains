<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8" lang="en">
	<title>Главная</title>
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
			<h1>Главная</h1><br/>
			<h3>Ссылки на JSF страницы:</h3><br/>
			<ul style="list-style-type: none">
				<li>
					<a href="<c:url value="/faces/cdi/products.xhtml"/>">Products CDI</a>
				</li>
				<li>
					<a href="<c:url value="/faces/managed/products.xhtml"/>">Products Managed</a>
				</li>
				<li>
					<a href="<c:url value="/faces/cdi/categories.xhtml"/>">Categories CDI</a>
				</li>
				<li>
					<a href="<c:url value="/faces/managed/categories.xhtml"/>">Categories Managed</a>
				</li>
			</ul>
		</div>
	</main>
	
	<footer class="footer padding-site">
		<%@ include file="fragments/copyright.jsp" %>
	</footer>
</div>
</body>
</html>