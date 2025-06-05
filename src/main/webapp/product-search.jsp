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
        .search-form {
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 20px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        .form-group input, .form-group select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .filter-group {
            display: flex;
            gap: 10px;
            margin-bottom: 15px;
        }
        .filter-group .form-group {
            flex: 1;
        }
        .product-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .product-card {
            background-color: white;
            border-radius: 5px;
            overflow: hidden;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            transition: transform 0.3s;
        }
        .product-card:hover {
            transform: translateY(-5px);
        }
        .product-card img {
            width: 100%;
            height: 200px;
            object-fit: cover;
        }
        .product-info {
            padding: 15px;
        }
        .product-name {
            font-weight: bold;
            margin-bottom: 5px;
            font-size: 1.1em;
        }
        .product-price {
            color: #e63946;
            font-weight: bold;
            margin-bottom: 5px;
        }
        .product-stock {
            font-size: 0.9em;
            color: #666;
        }
        .pagination {
            display: flex;
            justify-content: center;
            margin-top: 20px;
        }
        .pagination a, .pagination span {
            display: inline-block;
            padding: 8px 12px;
            margin: 0 5px;
            border-radius: 3px;
            text-decoration: none;
            color: #333;
        }
        .pagination a {
            background-color: #fff;
            border: 1px solid #ddd;
        }
        .pagination a:hover {
            background-color: #f5f5f5;
        }
        .pagination .active {
            background-color: #007bff;
            border: 1px solid #007bff;
            color: white;
        }
        .no-results {
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            text-align: center;
            margin-bottom: 20px;
        }
        .sort-controls {
            display: flex;
            justify-content: flex-end;
            margin-bottom: 15px;
        }
        .sort-controls select {
            padding: 5px;
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-left: 10px;
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
        <h1>Product Search</h1>
        
        <div class="search-form">
            <form action="/products/search" method="get">
                <div class="form-group">
                    <label for="name">Product Name:</label>
                    <input type="text" id="name" name="name" value="${searchTerm}" placeholder="Search by product name...">
                </div>
                <div class="filter-group">
                    <div class="form-group">
                        <label for="minPrice">Min Price:</label>
                        <input type="number" id="minPrice" name="minPrice" value="${minPrice}" min="0" step="0.01" placeholder="Min price">
                    </div>
                    <div class="form-group">
                        <label for="maxPrice">Max Price:</label>
                        <input type="number" id="maxPrice" name="maxPrice" value="${maxPrice}" min="0" step="0.01" placeholder="Max price">
                    </div>
                </div>
                <div class="filter-group">
                    <div class="form-group">
                        <label for="sortBy">Sort By:</label>
                        <select id="sortBy" name="sortBy">
                            <option value="name" ${sortBy == 'name' ? 'selected' : ''}>Name</option>
                            <option value="unitPrice" ${sortBy == 'unitPrice' ? 'selected' : ''}>Price</option>
                            <option value="unitInStock" ${sortBy == 'unitInStock' ? 'selected' : ''}>Stock</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="direction">Direction:</label>
                        <select id="direction" name="direction">
                            <option value="ASC" ${direction == 'ASC' ? 'selected' : ''}>Ascending</option>
                            <option value="DESC" ${direction == 'DESC' ? 'selected' : ''}>Descending</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <button type="submit" style="padding: 8px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer;">Search</button>
                    <button type="button" onclick="location.href='/products'" style="padding: 8px 15px; background-color: #6c757d; color: white; border: none; border-radius: 4px; cursor: pointer; margin-left: 10px;">Reset</button>
                </div>
            </form>
        </div>
        
        <c:if test="${empty productPage.content}">
            <div class="no-results">
                <h3>No products found matching your criteria</h3>
                <p>Try adjusting your search filters or browse all products.</p>
            </div>
        </c:if>
        
        <c:if test="${not empty productPage.content}">
            <div class="sort-controls">
                <span>Showing ${productPage.numberOfElements} of ${productPage.totalElements} products</span>
            </div>
            
            <div class="product-grid">
                <c:forEach var="product" items="${productPage.content}">
                    <div class="product-card">
                        <a href="/products/details?id=${product.id}">
                            <img src="${product.thumbnailUrl}" alt="${product.name}">
                        </a>
                        <div class="product-info">
                            <div class="product-name">${product.name}</div>
                            <div class="product-price">$${product.unitPrice}</div>
                            <div class="product-stock">
                                <c:choose>
                                    <c:when test="${product.unitInStock > 10}">
                                        In Stock (${product.unitInStock})
                                    </c:when>
                                    <c:when test="${product.unitInStock > 0}">
                                        Low Stock (${product.unitInStock})
                                    </c:when>
                                    <c:otherwise>
                                        Out of Stock
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            
            <!-- Pagination -->
            <div class="pagination">
                <c:if test="${currentPage > 0}">
                    <a href="/products/search?name=${searchTerm}&minPrice=${minPrice}&maxPrice=${maxPrice}&page=${currentPage - 1}&size=${size}&sortBy=${sortBy}&direction=${direction}">&laquo; Previous</a>
                </c:if>
                
                <c:forEach begin="0" end="${totalPages - 1}" var="i">
                    <c:choose>
                        <c:when test="${currentPage == i}">
                            <span class="active">${i + 1}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="/products/search?name=${searchTerm}&minPrice=${minPrice}&maxPrice=${maxPrice}&page=${i}&size=${size}&sortBy=${sortBy}&direction=${direction}">${i + 1}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                
                <c:if test="${currentPage < totalPages - 1}">
                    <a href="/products/search?name=${searchTerm}&minPrice=${minPrice}&maxPrice=${maxPrice}&page=${currentPage + 1}&size=${size}&sortBy=${sortBy}&direction=${direction}">Next &raquo;</a>
                </c:if>
            </div>
        </c:if>
    </div>
</body>
</html>
