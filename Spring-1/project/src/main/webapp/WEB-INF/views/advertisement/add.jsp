<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<c:set value="${pageContext.request.contextPath}" var="contextPath"/>
<c:set value="${contextPath}/resources" var="resPath"/>

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

<script type="text/javascript">
    $(document).ready(function () {
        CKEDITOR.replace('content');
        CKEDITOR.config.width = "100%";
        CKEDITOR.config.height = 600;
    });
</script>
