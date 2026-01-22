# Solvex AI Assistant

This is a RAGFlow-based AI Assistant web application with Spring Boot Backend and Vue 3 Frontend.

## Prerequisites

- Node.js (v18+)
- Java JDK 21
- Maven
- MySQL 8.0+

## Project Structure

- `frontend/`: Vue 3 + TypeScript + Tailwind CSS
- `backend/`: Spring Boot 3.5 + Spring Security + Data JPA

## Setup Instructions

### Database

1. Ensure MySQL is running.
2. Create database `ragflow_db`.
3. Update `backend/src/main/resources/application.properties` with your database credentials.

### Backend

```bash
cd backend
mvn spring-boot:run
```

Server runs on `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`.

## Features

- **User Authentication**: Login, Register, JWT-based security.
- **Chat Interface**: Real-time chat with AI.
- **RAGFlow Integration**: Mocked service integration (configure API URL in properties).
- **Citations**: View source documents for AI answers.
- **Session Management**: History of conversations.
- **Ads**: Designated ad spaces.
