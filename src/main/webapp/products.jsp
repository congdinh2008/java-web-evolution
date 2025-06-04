<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Products - ViVu Store</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
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
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }
        h1 {
            color: #333;
        }
        .products-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            gap: 20px;
        }
        .product-card {
            background-color: white;
            border-radius: 8px;
            padding: 15px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            transition: transform 0.3s ease;
        }
        .product-card:hover {
            transform: translateY(-5px);
        }
        .product-image {
            width: 100%;
            height: 200px;
            object-fit: cover;
            border-radius: 5px;
        }
        .product-price {
            font-weight: bold;
            color: #e63946;
            font-size: 1.2em;
        }
        .stock-info {
            color: #666;
            font-size: 0.9em;
        }
        .out-of-stock {
            color: #e63946;
        }
        .in-stock {
            color: #2a9d8f;
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
        <h1>Our Products</h1>
        
        <div class="products-grid">
            <c:forEach var="product" items="${products}">
                <div class="product-card">
                    <img src="${product.thumbnailUrl}" alt="${product.name}" class="product-image">
                    <h3>${product.name}</h3>
                    <p class="product-price">$${product.unitPrice}</p>
                    <p class="stock-info">
                        <c:choose>
                            <c:when test="${product.unitInStock > 0}">
                                <span class="in-stock">In Stock: ${product.unitInStock} units</span>
                            </c:when>
                            <c:otherwise>
                                <span class="out-of-stock">Out of Stock</span>
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </c:forEach>
        </div>
    </div>
</body>
</html>
