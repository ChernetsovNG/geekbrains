<%@ page contentType="text/html; ISO-8859-1; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set value="${pageContext.request.contextPath}" var="contextPath"/>
<div id="templatemo_menu">
    <ul>
        <li><a href="${contextPath}/">Главная</a></li>
        <li><a href="${contextPath}/advertisements/add">Добавить объявление</a></li>
    </ul>
</div>
