<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set value="${pageContext.request.contextPath}" var="contextPath"/>
<c:set value="${contextPath}/resources" var="resPath"/>
<!DOCTYPE >
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Geekbrains Spring-1 Demo</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js" crossorigin="anonymous"></script>
    <link href="${resPath}/style.css" rel="stylesheet" type="text/css"/>
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
    <div id="templatemo_content_wrapper">

        <%--<div id="templatemo_content">
            <h4>Объявления</h4>
            <ul class="templatemo_list">
                <c:if test="${not empty advertisements}">
                    <c:forEach items="${advertisements}" var="advertisement">
                        <li><a class="advertisement_ref"
                               href="./advertisements/${advertisement.id}">${advertisement.title}</a></li>
                    </c:forEach>
                </c:if>
            </ul>
        </div>--%>

        <div id="templatemo_sidebar_one">
            <h4>Категории</h4>
            <ul class="templatemo_list">
                <c:if test="${not empty categories}">
                    <c:forEach items="${categories}" var="category">
                        <li><a class="category_ref" href="./categories/${category.id}">${category.name}</a></li>
                    </c:forEach>
                </c:if>
            </ul>
            <div class="cleaner_h40"></div>
        </div>

        <div id="templatemo_sidebar_two">

            <div class="banner_250x200">
                <a href="" target="_parent"><img src="${resPath}/images/250x200_banner.jpg" alt="templates"/></a>
            </div>

            <div class="banner_125x125">
                <a href="" target="_parent"><img src="${resPath}/images/templatemo_ads.jpg" alt="web 1"/></a>
                <a href="" target="_parent"><img src="${resPath}/images/templatemo_ads.jpg" alt="web 2"/></a>
                <a href="" target="_parent"><img src="${resPath}/images/templatemo_ads.jpg" alt="templates 2"/></a>
                <a href="" target="_parent"><img src="${resPath}/images/templatemo_ads.jpg" alt="templates 1"/></a>
            </div>

            <div class="cleaner_h40"></div>

        </div>

        <div class="cleaner"></div>
    </div>
</div>
<button class="btn_load">Загрузить еще</button>

<jsp:include page="../elements/footer.jsp"/>

<script>
    var url = "./advertisements/advertisements_ajax";
    var contextPath = "${contextPath}";
</script>
<script src="${resPath}/assets/getData.js"></script>

</body>
</html>