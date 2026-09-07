# FinSight Backend

Spring Boot REST API for the **FinSight** personal finance application.

FinSight is a personal finance dashboard that allows users to import bank transactions from CSV files, automatically categorize expenses, and visualize their financial data through charts and summaries.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security (JWT authentication)
- PostgreSQL
- Maven

## Features (MVP)

- User registration and login.
- JWT authentication.
- Import bank transactions from CSV.
- Store transactions in PostgreSQL.
- Automatic transaction categorization.
- REST API for dashboard statistics.
- Budget management.

## Project Structure

```text
src
├── controller
├── service
├── repository
├── model
├── dto
├── security
├── config
└── util
```

## Getting Started

### 1. Clone repository

```bash
git clone <repository-url>
cd finsight-backend
```

### 2. Configure PostgreSQL

Create a PostgreSQL database.

Example:

```sql
CREATE DATABASE finsight;
```

Create an `application.yml` file inside `src/main/resources`.

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8080
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

Backend starts at:

```text
http://localhost:8080
```

## Planned API Endpoints

### Authentication

| Method | Endpoint             | Description   |
| ------ | -------------------- | ------------- |
| POST   | `/api/auth/register` | Register user |
| POST   | `/api/auth/login`    | Login user    |

### Transactions

| Method | Endpoint                   | Description             |
| ------ | -------------------------- | ----------------------- |
| GET    | `/api/transactions`        | Get user's transactions |
| POST   | `/api/transactions/import` | Import CSV transactions |
| PATCH  | `/api/transactions/{id}`   | Update category         |

### Dashboard

| Method | Endpoint                    | Description                 |
| ------ | --------------------------- | --------------------------- |
| GET    | `/api/dashboard/summary`    | Income, expenses, balance   |
| GET    | `/api/dashboard/categories` | Expenses by category        |
| GET    | `/api/dashboard/monthly`    | Monthly spending statistics |

### Budgets

| Method | Endpoint            | Description   |
| ------ | ------------------- | ------------- |
| GET    | `/api/budgets`      | List budgets  |
| POST   | `/api/budgets`      | Create budget |
| PUT    | `/api/budgets/{id}` | Update budget |

## Database (Initial MVP)

Entities:

- User
- Transaction
- Category (planned)
- Budget (planned)

Relationship:

```text
User
 └── Transactions (1:N)
```

## Development Workflow

- Create feature branches from `develop`.
- Open a Pull Request before merging.
- Keep commits small and descriptive.

Example commit messages:

```text
feat: add transaction entity
feat: implement CSV parser
fix: validate uploaded CSV format
docs: update API documentation
```

## Team

**FinSight** — Software Project 2 (Haaga-Helia UAS)

Backend repository maintained by the FinSight development team.
