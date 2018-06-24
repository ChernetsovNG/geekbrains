<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<c:set value="${pageContext.request.contextPath}" var="contextPath"/>
<c:set value="${contextPath}/resources" var="resPath"/>

<!DOCTYPE >
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>IT SITE</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"
            crossorigin="anonymous"></script>
    <link href="${resPath}/style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<div id="templatemo_header_wrapper">
    <tiles:insertAttribute name="header"/>
    <tiles:insertAttribute name="menu"/>
</div>
<div id="templatemo_main_wrapper">
    <div id="templatemo_content_wrapper">
        <div id="templatemo_content">
            <tiles:insertAttribute name="content" ignore="true"/>
            <tiles:insertAttribute name="advertisementBodyTemplate" ignore="true"/>
        </div>
        <tiles:insertAttribute name="sideBarOne" ignore="true"/>
        <tiles:insertAttribute name="sideBarTwo" ignore="true"/>
        <div class="cleaner"></div>
    </div>
</div>
<tiles:insertAttribute name="loader" ignore="true"/>
<div id="templatemo_footer_wrapper">
    <tiles:insertAttribute name="footer"/>
</div>
</body>
</html>
