<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<spring:message code="label_create_advertisement" var="labelCreate"/>
<spring:message code="label_category" var="labelCategory"/>
<spring:message code="label_choice_category" var="labelChoiceCategory"/>
<spring:message code="label_title" var="labelTitle"/>
<spring:message code="label_company_data" var="labelCompanyData"/>
<spring:message code="label_company_name" var="labelCompanyName"/>
<spring:message code="label_company_description" var="labelCompanyDescription"/>
<spring:message code="label_company_address" var="labelCompanyAddress"/>
<spring:message code="label_button_publish" var="labelButtonPublish"/>

<c:set value="${pageContext.request.contextPath}" var="contextPath"/>
<c:set value="${contextPath}/resources" var="resPath"/>

<div class="post_section">
    <form:form modelAttribute="advertisement" class="add_advertisement_form" method="POST"
               action="${contextPath}/advertisements">
        <h2 class="message">${labelCreate}</h2>

        <strong class="add_category">${labelCategory}*</strong>
        <select id="categoryId" name="categoryId" class="cd-select">
            <c:if test="${not empty categories}">
                <option value="0" selected>${labelChoiceCategory}</option>
                <c:forEach items="${categories}" var="category">
                    <option value="${category.id}">${category.name}</option>
                </c:forEach>
            </c:if>
        </select>
        <p>
            <form:label path="title" class="add_title">${labelTitle}*</form:label>
                <form:input type="text" path="title" class="add_title_input"/>
                <form:errors path="title" cssClass="error"/>
        <p style="padding-top:50px;">
                <form:textarea path="content" id="content" class="contentarea"/>

        <div class="company_add">
            <span class="company_info_title">${labelCompanyData}</span>
            <form:input path="company.name" type="text" placeholder="${labelCompanyName}" class="add_company_name"/>
            <form:input path="company.description" type="text" placeholder="${labelCompanyDescription}"
                        name="add_company_description"/>
            <form:input path="company.address" type="text" placeholder="${labelCompanyAddress}"
                        name="add_company_address"/>
        </div>
        <input type="submit" class="button_sub" value="${labelButtonPublish}"/>
    </form:form>
</div>

<c:if test="${not empty message}">
    <span class="error">${message}</span>
</c:if>

<script type="text/javascript">
    $(document).ready(function () {
        CKEDITOR.replace('content');
        CKEDITOR.config.width = "100%";
        CKEDITOR.config.height = 600;
    });
</script>
