# AI Customer Support Chatbot

AI-powered customer support chatbot built using Spring Boot, PostgreSQL, JWT Authentication and Groq AI.

## Features

* JWT Authentication
* Role Based Access Control (RBAC)
* AI Chat Support using Groq API
* FAQ Management
* Support Ticket Management
* Admin Dashboard
* Swagger API Documentation

## Tech Stack

* Java 21
* Spring Boot
* Spring Security
* PostgreSQL
* JWT
* Groq API
* Maven

## Environment Variables

DB_HOST=localhost

DB_PORT=5432

DB_NAME=aichatbot

DB_USERNAME=postgres

DB_PASSWORD=your_password

JWT_SECRET=your_jwt_secret

GROQ_API_KEY=your_groq_api_key

## Run Application

mvn spring-boot:run

## Swagger UI

http://localhost:8080/swagger-ui.html
