<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>About Us - ViVu Store</title>
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
            max-width: 800px;
            margin: 0 auto;
            padding: 0 20px;
        }
        h1 {
            color: #333;
        }
        .about-section {
            background-color: white;
            border-radius: 8px;
            padding: 25px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        .team-section {
            background-color: white;
            border-radius: 8px;
            padding: 25px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
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
        <h1>About ViVu Store</h1>
        
        <div class="about-section">
            <h2>Our Story</h2>
            <p>
                ViVu Store was founded in 2020 with a simple mission: to provide high-quality products at affordable prices. 
                What started as a small online shop has grown into a trusted brand serving customers nationwide.
            </p>
            <p>
                Our commitment to customer satisfaction and product excellence has been the cornerstone of our success. 
                We carefully select each item in our inventory to ensure it meets our rigorous standards for quality and value.
            </p>
        </div>
        
        <div class="team-section">
            <h2>Our Team</h2>
            <p>
                ViVu Store is powered by a dedicated team of professionals who are passionate about what they do. 
                From our customer service representatives to our logistics team, everyone works together to ensure 
                that your shopping experience is seamless and enjoyable.
            </p>
            <p>
                We believe in fostering a collaborative and innovative work environment where ideas flourish and 
                creativity is encouraged. This approach has enabled us to constantly improve our services and stay 
                ahead of industry trends.
            </p>
        </div>
    </div>
</body>
</html>
