<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<c:set value="${pageContext.request.contextPath}" var="contextPath"/>
<c:set value="${contextPath}/resources" var="resPath"/>

<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Geekbrains Spring-1 Demo</title>
    <link href="${resPath}/style.css" rel="stylesheet" type="text/css"/>
    <script src="${resPath}/assets/ckeditor/ckeditor.js"></script>
    <script src="https://code.jquery.com/jquery-3.3.1.min.js"
            integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8=" crossorigin="anonymous"></script>
</head>
<body>
<div id="templatemo_header_wrapper">
    <div id="templatemo_header">
        <div id="site_title">
        </div>

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
            <div class="post_section">
                <form:form modelAttribute="advertisement" class="add_advertisement_form" method="POST"
                           action="${contextPath}/advertisements">
                    <h2 class="message">Создание объявления</h2>

                    <strong class="add_category">Категория*</strong>
                    <select id="categoryId" name="categoryId" class="cd-select">
                        <c:if test="${not empty categories}">
                            <option value="0" selected>Выберите категорию</option>
                            <c:forEach items="${categories}" var="category">
                                <option value="${category.id}">${category.name}</option>
                            </c:forEach>
                        </c:if>
                    </select>
                    <p>
                        <form:label path="title" class="add_title">Заголовок*</form:label>
                            <form:input type="text" path="title" class="add_title_input"/>
                    <p style="padding-top:50px;">
                            <form:textarea path="content" id="content" class="contentarea"/>

                    <div class="company_add">
                        <span class="company_info_title">Данные компании*</span>
                        <form:input path="company.name" type="text" placeholder="Название" class="add_company_name"/>
                        <form:input path="company.description" type="text" placeholder="Описание"
                                    name="add_company_description"/>
                        <form:input path="company.address" type="text" placeholder="Адрес" name="add_company_address"/>
                    </div>
                    <input type="submit" class="button_sub" value="Опубликовать"/>
                </form:form>
            </div>
            <div class="cleaner_h40"><!-- a spacing between 2 posts --></div>
        </div>
        <div class="cleaner_h40"></div>
    </div>
    <div class="cleaner"></div>
</div>

<jsp:include page="../elements/footer.jsp"/>

<script type="text/javascript">
    $(document).ready(function () {
        CKEDITOR.replace('content');
        CKEDITOR.config.width = "100%";
        CKEDITOR.config.height = 600;
    });
</script>

</body>