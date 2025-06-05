<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${title}</title>
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
        h1 {
            color: #333;
            margin-bottom: 20px;
        }
        .alert {
            background-color: #f8d7da;
            color: #721c24;
            padding: 10px 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 30px;
            background-color: white;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        th, td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f8f9fa;
            font-weight: bold;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .stock-warning {
            color: #e63946;
            font-weight: bold;
        }
        .out-of-stock {
            background-color: #f8d7da;
        }
        .low-stock {
            background-color: #fff3cd;
        }
        .actions {
            display: flex;
            gap: 10px;
        }
        .action-button {
            padding: 6px 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 0.9em;
        }
        .edit-button {
            background-color: #28a745;
            color: white;
        }
        .threshold-form {
            background-color: white;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 15px;
        }
        .threshold-form label {
            font-weight: bold;
        }
        .threshold-form input {
            width: 80px;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .threshold-form button {
            padding: 8px 15px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .no-items {
            background-color: white;
            padding: 20px;
            text-align: center;
            border-radius: 5px;
        }
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            text-decoration: none;
            color: #007bff;
        }
        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <nav>
                <a href="/">Home</a>
                <a href="/products">Products</a>
                <a href="/about">About</a>
                <a href="/contact">Contact</a>
            </nav>
        </div>
    </header>

    <div class="container">
        <a href="/products" class="back-link">&larr; Back to Products</a>
        
        <h1>Low Stock Products (Threshold: ${threshold})</h1>
        
        <div class="threshold-form">
            <form action="/products/low-stock" method="get">
                <label for="threshold">Set stock threshold:</label>
                <input type="number" id="threshold" name="threshold" value="${threshold}" min="1">
                <button type="submit">Apply</button>
            </form>
        </div>
        
        <c:if test="${empty products}">
            <div class="no-items">
                <h3>No products below stock threshold</h3>
                <p>All products have stock levels above the threshold of ${threshold}.</p>
            </div>
        </c:if>
        
        <c:if test="${not empty products}">
            <div class="alert">
                There are ${products.size()} products that need attention due to low stock levels.
            </div>
            
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Product</th>
                        <th>Price</th>
                        <th>Stock</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="product" items="${products}">
                        <tr class="${product.unitInStock == 0 ? 'out-of-stock' : 'low-stock'}">
                            <td>${product.id}</td>
                            <td>${product.name}</td>
                            <td>$${product.unitPrice}</td>
                            <td class="stock-warning">
                                <c:choose>
                                    <c:when test="${product.unitInStock == 0}">
                                        OUT OF STOCK
                                    </c:when>
                                    <c:otherwise>
                                        ${product.unitInStock} units
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="actions">
                                <a href="/products/edit?id=${product.id}" class="action-button edit-button">Update Stock</a>
                                <a href="/products/details?id=${product.id}" class="action-button" style="background-color: #6c757d; color: white;">View Details</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
</body>
</html>
