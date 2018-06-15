<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set value="${pageContext.request.contextPath}" var="contextPath"/>
<c:set value="${contextPath}/resources" var="resPath"/>

<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Geekbrains Spring-1 Demo</title>

    <link href="${resPath}/style.css" rel="stylesheet" type="text/css"/>
    <script src="${resPath}/assets/ckeditor/ckeditor.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js" crossorigin="anonymous"></script>
</head>
<body>
<div id="templatemo_header_wrapper">
    <div id="templatemo_header">
        <div id="site_title"></div>
        <div id="templatemo_rss">
            <a href="" target="_parent">SUBSCRIBE<br/><span>OUR FEED</span></a>
        </div>
    </div>

    <div id="templatemo_menu">
        <ul>
            <li><a href="${contextPath}/">Главная</a></li>
            <li><a href="${contextPath}/advertisements/add">Добавить объявление</a></li>
        </ul>
    </div>
</div>

<div id="templatemo_main_wrapper">
    <div id="templatemo_add_content_wrapper">

        <div id="templatemo_content">

            <c:if test="${not empty advertisement}">

                <div class='post_section view'>
                    <h2><a class='advertisement__title' href=''></a>${advertisement.title}</h2>
                    <strong>Дата: <span></span></strong>
                    <span class='advertisement__date'>
                        <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${advertisement.publishedDate}"/>
                    </span>
                    <br/>
                    <strong>Компания: </strong>
                    <span class='advertisement__company'>
                        Название: <span>${advertisement.company.name}</span>
                        Описание: <span>${advertisement.company.description}</span>
                        Адрес: <span>${advertisement.company.address}</span>
                    </span>
                    <div class="cleaner"></div>
                    <p>
                        <strong>Содержание объявления: </strong>
                    <div class='advertisement__content view'>${advertisement.content}</div>
                    <div class='cleaner'></div>
                    <p>
                    <div class='category view'>
                        <strong>Категория: </strong>
                        <span>${advertisement.category.name}</span>
                    </div>
                </div>
            </c:if>
        </div>
        <div class="cleaner"></div>
    </div>
</div>

<jsp:include page="../elements/footer.jsp"/>

</body>
