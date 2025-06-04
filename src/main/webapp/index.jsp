<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${title}</title>
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
        .hero-section {
            background-color: white;
            border-radius: 8px;
            padding: 30px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            margin-bottom: 30px;
            text-align: center;
        }
        .hero-section h1 {
            font-size: 2.5em;
            margin-bottom: 15px;
        }
        .cta-button {
            display: inline-block;
            padding: 12px 24px;
            background-color: #e63946;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-weight: bold;
            margin-top: 15px;
            transition: background-color 0.3s ease;
        }
        .cta-button:hover {
            background-color: #d62828;
        }
        .features {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .feature-card {
            background-color: white;
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            text-align: center;
        }
        .feature-card h3 {
            margin-top: 0;
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
        <div class="hero-section">
            <h1>${message}</h1>
            <p>${description}</p>
            <a href="${pageContext.request.contextPath}/products" class="cta-button">View Our Products</a>
        </div>
        
        <div class="features">
            <div class="feature-card">
                <h3>Quality Products</h3>
                <p>We offer only the highest quality products, carefully selected to meet your needs.</p>
            </div>
            
            <div class="feature-card">
                <h3>Fast Shipping</h3>
                <p>Enjoy quick delivery with our efficient shipping services across the country.</p>
            </div>
            
            <div class="feature-card">
                <h3>24/7 Support</h3>
                <p>Our customer support team is available around the clock to assist you.</p>
            </div>
        </div>
    </div>
</body>
</html>