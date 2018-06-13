<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<c:set value="${pageContext.request.contextPath}" var="contextPath"/>
<c:set value="${contextPath}/resources" var="resPath"/>
<html>
<head>
    <title>Main page</title>
</head>
<body>
<c:if test="${not empty categories}">
    <c:forEach items="${categories}" var="category">
        <li>
            <a class="category_ref" href="./categories/${category.id}">${category.name}</a>
        </li>
    </c:forEach>
</c:if>
</body>
</html>
