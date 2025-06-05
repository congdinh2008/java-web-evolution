<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${empty product ? 'Add New Product' : 'Edit Product'} - ViVu Store</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #f5f5f5;
        }
        header {
            background-color: #333;
            color: white;
            padding: 8px 0;
            margin-bottom: 20px;
        }
        nav {
            display: flex;
            justify-content: center;
            padding: 8px;
        }
        nav a {
            color: white;
            text-decoration: none;
            margin: 0 16px;
        }
        nav a:hover {
            text-decoration: underline;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 0 20px;
        }
        h1 {
            color: #333;
        }
        .form-section {
            background-color: white;
            border-radius: 8px;
            padding: 25px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        .product-form {
            display: grid;
            gap: 16px;
        }
        .form-group {
            display: flex;
            flex-direction: column;
        }
        label {
            margin-bottom: 5px;
            font-weight: bold;
        }
        input, textarea {
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        button {
            padding: 8px 16px;
            background-color: #333;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
        }
        button:hover {
            background-color: #555;
        }
        .buttons {
            display: flex;
            gap: 8px;
            margin-top: 16px;
        }
        .cancel-button {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 8px 16px;
            border-radius: 4px;
            background-color: #777;
            text-decoration: none;
            color: white;
        }
        .error-message {
            color: #e63946;
            font-weight: bold;
            margin-bottom: 16px;
        }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <nav>
                <a href="${pageContext.request.contextPath}/">Home</a>
                <a href="${pageContext.request.contextPath}/products">Products</a>
                <a href="${pageContext.request.contextPath}/about">About Us</a>
                <a href="${pageContext.request.contextPath}/contact">Contact</a>
            </nav>
        </div>
    </header>

    <div class="container">
        <h1>${empty product ? 'Add New Product' : 'Edit Product'}</h1>
        
        <c:if test="${not empty errorMessage}">
            <div class="error-message">${errorMessage}</div>
        </c:if>
        
        <div class="form-section">
            <form class="product-form" method="post" action="${pageContext.request.contextPath}${empty product ? '/products/add' : '/products/update'}">
                <c:if test="${not empty product}">
                    <input type="hidden" name="id" value="${product.id}">
                </c:if>
                
                <div class="form-group">
                    <label for="name">Product Name</label>
                    <input type="text" id="name" name="name" required value="${product.name}">
                </div>
                
                <div class="form-group">
                    <label for="unitPrice">Unit Price</label>
                    <input type="number" id="unitPrice" name="unitPrice" step="0.01" min="0" required value="${product.unitPrice}">
                </div>
                
                <div class="form-group">
                    <label for="unitInStock">Units In Stock</label>
                    <input type="number" id="unitInStock" name="unitInStock" min="0" required value="${product.unitInStock}">
                </div>
                
                <div class="form-group">
                    <label for="thumbnailUrl">Thumbnail URL</label>
                    <input type="url" id="thumbnailUrl" name="thumbnailUrl" required value="${empty product ? 'https://placehold.co/600x400?text=New+Product' : product.thumbnailUrl}">
                </div>
                
                <div class="buttons">
                    <button type="submit">${empty product ? 'Add Product' : 'Update Product'}</button>
                    <a href="${pageContext.request.contextPath}/products" class="button cancel-button">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
