<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Contact Us - ViVu Store</title>
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
            max-width: 800px;
            margin: 0 auto;
            padding: 0 20px;
        }
        h1 {
            color: #333;
        }
        .contact-section {
            background-color: white;
            border-radius: 8px;
            padding: 25px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        .contact-form {
            display: grid;
            gap: 15px;
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
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        button {
            padding: 10px 15px;
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
        .contact-info {
            background-color: white;
            border-radius: 8px;
            padding: 25px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        .info-item {
            margin-bottom: 15px;
        }
        .info-item h3 {
            margin-bottom: 5px;
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
        <h1>Contact Us</h1>
        
        <div class="contact-section">
            <h2>Send Us a Message</h2>
            <form class="contact-form">
                <div class="form-group">
                    <label for="name">Your Name</label>
                    <input type="text" id="name" name="name" required>
                </div>
                
                <div class="form-group">
                    <label for="email">Your Email</label>
                    <input type="email" id="email" name="email" required>
                </div>
                
                <div class="form-group">
                    <label for="subject">Subject</label>
                    <input type="text" id="subject" name="subject" required>
                </div>
                
                <div class="form-group">
                    <label for="message">Message</label>
                    <textarea id="message" name="message" rows="5" required></textarea>
                </div>
                
                <button type="submit">Send Message</button>
            </form>
        </div>
        
        <div class="contact-info">
            <h2>Contact Information</h2>
            
            <div class="info-item">
                <h3>Address</h3>
                <p>123 ViVu Street, District 1, Ho Chi Minh City, Vietnam</p>
            </div>
            
            <div class="info-item">
                <h3>Phone</h3>
                <p>+84 (0) 123 456 789</p>
            </div>
            
            <div class="info-item">
                <h3>Email</h3>
                <p>info@vivustore.com</p>
            </div>
            
            <div class="info-item">
                <h3>Business Hours</h3>
                <p>Monday - Friday: 9:00 AM - 6:00 PM</p>
                <p>Saturday: 9:00 AM - 1:00 PM</p>
                <p>Sunday: Closed</p>
            </div>
        </div>
    </div>
</body>
</html>
