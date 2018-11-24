-- MySQL syntax
DROP DATABASE IF EXISTS BOOKS;
CREATE DATABASE BOOKS;
USE BOOKS;
GRANT <SELECT, CREATE, UPDATE, DELETE, ALL> PRIVILEGES ON mysql.proc TO 'schemacrawler'@'%';
FLUSH PRIVILEGES;