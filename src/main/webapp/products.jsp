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

                .flex {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 20px;
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
                    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
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

                .buttons {
                    display: flex;
                    justify-content: space-between;
                    margin-top: 10px;
                }

                .button {
                    display: inline-block;
                    padding: 8px 16px;
                    background-color: #3498db;
                    color: white;
                    text-decoration: none;
                    border-radius: 4px;
                    margin-top: 10px;
                }

                .button:hover {
                    background-color: #2980b9;
                }

                .delete-button {
                    background-color: #e74c3c;
                    border: none;
                    color: white;
                    padding: 8px 16px;
                    border-radius: 4px;
                    cursor: pointer;
                    font-size: 16px;
                }

                .delete-button:hover {
                    background-color: #c0392b;
                }

                .edit-button {
                    background-color: #f39c12;
                    color: white;
                    padding: 8px 16px;
                    border-radius: 4px;
                    text-decoration: none;
                }

                .edit-button:hover {
                    background-color: #e67e22;
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
                <div class="flex">
                    <h1>Our Products</h1>
                    <a href="${pageContext.request.contextPath}/products/add" class="button">Add New Product</a>
                </div>

                <div class="products-grid">
                    <c:forEach var="product" items="${products}">
                        <div class="product-card">
                            <a href="${pageContext.request.contextPath}/products/details?id=${product.id}"
                                style="text-decoration: none; color: inherit;">
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
                            </a>
                            <div class="buttons">
                                <a href="${pageContext.request.contextPath}/products/edit?id=${product.id}"
                                    class="button edit-button">Edit</a>
                                <form method="post" action="${pageContext.request.contextPath}/products/delete">
                                    <input type="hidden" name="id" value="${product.id}">
                                    <button type="submit" class="button delete-button"
                                        onclick="return confirm('Are you sure you want to delete this product?');">Delete</button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </body>

        </html>