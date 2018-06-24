<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set value="${pageContext.request.contextPath}" var="contextPath"/>
<c:set value="${contextPath}/resources" var="resPath"/>

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
