<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set value="${pageContext.request.contextPath}" var="contextPath"/>
<c:set value="${contextPath}/resources" var="resPath"/>

<h2 class="category_name">${category.name}</h2>
<script>
    var url = "./${category.id}/advertisements_ajax";
    var contextPath = "${contextPath}";
</script>
<script src="${resPath}/assets/getData.js"></script>
