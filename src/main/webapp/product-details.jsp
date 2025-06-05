<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${product.name} - ViVu Store</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #f5f5f5;
        }
        header {
            background-color: #333;
            color: white;
            padding: 10px 0;
            margin-bottom: 20px;
        }
        nav {
            display: flex;
            justify-content: center;
            padding: 10px;
        }
        nav a {
            color: white;
            text-decoration: none;
            margin: 0 15px;
        }
        nav a:hover {
            text-decoration: underline;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
            padding: 0 20px;
        }
        h1 {
            color: #333;
        }
        .product-details {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 30px;
            background-color: white;
            border-radius: 8px;
            padding: 25px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        .product-image {
            width: 100%;
            height: auto;
            object-fit: cover;
            border-radius: 5px;
        }
        .product-info h2 {
            margin-top: 0;
            color: #333;
        }
        .product-price {
            font-size: 1.8em;
            color: #e63946;
            font-weight: bold;
            margin: 15px 0;
        }
        .stock-info {
            margin: 15px 0;
            padding: 10px;
            border-radius: 4px;
            font-weight: bold;
        }
        .in-stock {
            background-color: #e8f5e9;
            color: #2e7d32;
        }
        .low-stock {
            background-color: #fff8e1;
            color: #f57f17;
        }
        .out-of-stock {
            background-color: #ffebee;
            color: #c62828;
        }
        .action-buttons {
            display: flex;
            gap: 10px;
            margin-top: 20px;
        }
        .button {
            display: inline-block;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
            text-decoration: none;
            text-align: center;
        }
        .edit-button {
            background-color: #3498db;
            color: white;
        }
        .delete-button {
            background-color: #e74c3c;
            color: white;
        }
        .back-button {
            background-color: #95a5a6;
            color: white;
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
        <div class="product-details">
            <div>
                <img src="${product.thumbnailUrl}" alt="${product.name}" class="product-image">
            </div>
            <div class="product-info">
                <h2>${product.name}</h2>
                <p class="product-price">$${product.unitPrice}</p>
                
                <c:choose>
                    <c:when test="${product.unitInStock <= 0}">
                        <p class="stock-info out-of-stock">Out of Stock</p>
                    </c:when>
                    <c:when test="${product.unitInStock < 10}">
                        <p class="stock-info low-stock">Low Stock: ${product.unitInStock} units left</p>
                    </c:when>
                    <c:otherwise>
                        <p class="stock-info in-stock">In Stock: ${product.unitInStock} units</p>
                    </c:otherwise>
                </c:choose>
                
                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/products/edit?id=${product.id}" class="button edit-button">Edit Product</a>
                    <form method="post" action="${pageContext.request.contextPath}/products/delete" style="display: inline;">
                        <input type="hidden" name="id" value="${product.id}">
                        <button type="submit" class="button delete-button" onclick="return confirm('Are you sure you want to delete this product?')">Delete Product</button>
                    </form>
                    <a href="${pageContext.request.contextPath}/products" class="button back-button">Back to Products</a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
