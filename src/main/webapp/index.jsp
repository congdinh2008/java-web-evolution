<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>JSP Test Page</title>
</head>
<body>
    <h1>Hello from index.jsp!</h1>
    <p>This page was definitely reached.</p>
    <p>Test Message Attribute (from scriptlet): <%= request.getAttribute("message") %></p>
    <p>Test Message Attribute (from EL): ${message}</p>
    <p>Test Title Attribute (from EL): ${title}</p>
</body>
</html>