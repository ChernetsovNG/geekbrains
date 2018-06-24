<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<c:set value="${pageContext.request.contextPath}" var="contextPath"/>

<spring:message code="label_categories" var="labelCategories" />

<div id="templatemo_sidebar_one">
    <h4>${labelCategories}</h4>
    <ul class="templatemo_list">
        <c:if test="${not empty categories}">
            <c:forEach items="${categories}" var="category">
                <li><a class="category_ref" href="${contextPath}/categories/${category.id}">${category.name}</a></li>
            </c:forEach>
        </c:if>
    </ul>
    <div class="cleaner_h40"></div>
</div>
