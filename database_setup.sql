-- Drop existing tables if they exist to start fresh
IF OBJECT_ID('user_roles', 'U') IS NOT NULL
    DROP TABLE user_roles;

IF OBJECT_ID('users', 'U') IS NOT NULL
    DROP TABLE users;

IF OBJECT_ID('roles', 'U') IS NOT NULL
    DROP TABLE roles;

IF OBJECT_ID('products', 'U') IS NOT NULL
    DROP TABLE products;

IF OBJECT_ID('categories', 'U') IS NOT NULL
    DROP TABLE categories;

-- Create roles table
CREATE TABLE roles (
    id int IDENTITY(1,1) PRIMARY KEY,
    name varchar(50) NOT NULL UNIQUE,
    description varchar(255),
    created_at datetime2(6) NOT NULL DEFAULT GETDATE(),
    updated_at datetime2(6)
);

-- Create users table with correct constraints
CREATE TABLE users (
    id int IDENTITY(1,1) PRIMARY KEY,
    username varchar(50) NOT NULL UNIQUE,
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    phone_number varchar(15) NOT NULL,
    email varchar(100) NULL, -- Allow NULL for optional email
    password varchar(255) NOT NULL,
    address TEXT,
    enabled bit NOT NULL DEFAULT 1,
    account_non_expired bit NOT NULL DEFAULT 1,
    account_non_locked bit NOT NULL DEFAULT 1,
    credentials_non_expired bit NOT NULL DEFAULT 1,
    created_at datetime2(6) NOT NULL DEFAULT GETDATE(),
    updated_at datetime2(6),
    last_login datetime2(6)
);

-- Create user_roles junction table
CREATE TABLE user_roles (
    user_id int NOT NULL,
    role_id int NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Create categories table
CREATE TABLE categories (
    id int IDENTITY(1,1) PRIMARY KEY,
    name varchar(255) NOT NULL UNIQUE,
    description TEXT
);

-- Create products table
CREATE TABLE products (
    id int IDENTITY(1,1) PRIMARY KEY,
    name varchar(255) NOT NULL,
    unit_price float NOT NULL,
    unit_in_stock int NOT NULL,
    thumbnail_url varchar(255) NOT NULL,
    category_id int,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

PRINT 'Database schema created successfully.';
