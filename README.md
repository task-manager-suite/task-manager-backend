# Task Manager Backend

A clean and modular Spring Boot 3.x RESTful API for managing tasks.  
Implements task creation, retrieval, update, soft deletion, and filtering by status — all in memory (H2 DB) and following best REST practices.

---

## ✅ Features

- Create a new task
- Retrieve all tasks or filter by status
- Get a task by ID
- Update task details or status
- Soft delete (logical deletion) of tasks
- Input validation and custom exceptions
- Swagger-based API documentation
- Flyway migration for schema and seed data
- Unit tests with JUnit & Mockito

---

## 🧱 Tech Stack

- Java 21
- Spring Boot 3.3.x
- Spring Data JPA
- H2 In-Memory Database
- Flyway
- Swagger (SpringDoc OpenAPI)
- Lombok, ModelMapper
- JUnit 5, Mockito
- Maven

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java 21+
- Maven 3.8+

### 💻 Clone the repository

```bash
git clone https://github.com/task-manager-suite/task-manager-backend.git
cd task-manager-backend
```

### ▶️ Run the application

```bash
./mvnw spring-boot:run
```

or

```bash
mvn clean install
mvn spring-boot:run
```

---

## 🔧 API Endpoints

Base URL: `http://localhost:8080/api`

| Method | Endpoint                   | Description                   |
|--------|----------------------------|-------------------------------|
| POST   | `/tasks`                   | Create a new task             |
| GET    | `/tasks?page=1&offset=10` | Get all paginated tasks       |
| GET    | `/tasks?status=TODO`       | Get tasks filtered by status  |
| GET    | `/tasks/{id}`              | Get task by ID                |
| PUT    | `/tasks/{id}`              | Update task title/description |
| PATCH  | `/tasks/{id}/status`       | Update only task status       |
| DELETE | `/tasks/{id}`              | Soft delete task              |

---

## 📖 Swagger UI

> View the API documentation in your browser:

- [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/api/v3/api-docs](http://localhost:8080/api/v3/api-docs)

---

## 🧪 Running Tests

```bash
mvn test
```

Covered tests:

- ✅ `TaskServiceImplTest` – all service logic
- ✅ `TaskControllerTest` – all REST endpoints
- ✅ Global exception handler – error contract & validation

---

## 📁 Project Structure

```
src
├── main
│   ├── java
│   │   └── org.montadhahri.taskmanager
│   │       ├── config
│   │       ├── controller
│   │       ├── dto
│   │       │   ├── request
│   │       │   └── response
│   │       ├── entity
│   │       ├── enumeration
│   │       ├── exception
│   │       ├── repository
│   │       ├── service
│   │       └── util
│   └── resources
│       ├── application.properties
│       └── db/migration
│           ├── V1__init_schema.sql
│           └── V2__insert_seed_data.sql
└── test
    └── java
        └── org.montadhahri.taskmanager
            ├── service
            └── controller
```

---

## 🧼 Code Quality & Best Practices

- ✅ Layered architecture (controller, service, repository, config)
- ✅ DTOs for input/output boundaries
- ✅ JSR 380 bean validation (with `@Valid`)
- ✅ Centralized exception handling
- ✅ `ModelMapper` used for mapping entities ↔ DTOs
- ✅ `@Slf4j` logging throughout the service layer
- ✅ Unit tests for services and controllers
- ✅ JavaDoc and meaningful commit history

---

## 📌 Author

**Montassar Dhahri**  
- GitHub: [@montadhr](https://github.com/montadhr)  

---

## 📄 License

This project is delivered as part of a technical challenge. All rights reserved.