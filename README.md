# Personal Finance Management System

A robust Spring Boot application for managing personal finances with comprehensive budget tracking, transaction management, and financial analytics.

## Features

### Core Functionality
- **User Management**: Secure registration, authentication, and profile management with JWT-based authorization
- **Category Management**: Create, update, and organize transaction categories with merge and import capabilities
- **Transaction Tracking**: Full CRUD operations with advanced filtering (date range, amount range, category-based search)
- **Budget Management**: Create and track budgets per category with usage monitoring and active/inactive status control
- **Financial Analytics**: User statistics and budget usage insights

### Technical Highlights
- **CQRS Pattern**: Separation of command and query operations for better code organization
- **JWT Security**: Stateless authentication system with token-based authorization
- **Comprehensive Validation**: Jakarta Bean Validation for data integrity
- **RESTful API**: Well-structured endpoints with global exception handling
- **High Test Coverage**: 600+ tests with 93-94% coverage using JUnit5, AssertJ, and Spring Boot Test

## Technology Stack
- **Backend**: Spring Boot, Spring Security, Spring Data JPA
- **Database**: PostgreSQL (production), H2 (testing)
- **Authentication**: JWT (JSON Web Tokens)
- **API Documentation**: OpenAPI 3 (springdoc) with Swagger UI
- **Testing**: JUnit5, AssertJ, Jakarta Bean Validation
- **Architecture**: CQRS pattern, DTOs with mappers

## Project Structure
- **Entities**: User, Category, Transaction, Budget
- **DTOs**: Request/Response patterns with dedicated mappers
- **Services**: Command and Query separation following CQRS
- **Controllers**: RESTful endpoints with comprehensive error handling
- **Security**: Custom authentication filter and user details service

## API Features
- User authentication (register/login) with JWT tokens
- Category operations with search and merge capabilities
- Advanced transaction filtering and management
- Budget tracking with usage analytics
- Password management

## Configuration
For this CV/demo project the app ships with simple defaults.

| Variable     | Purpose                                | Default (fallback)                                      |
|--------------|-----------------------------------------|---------------------------------------------------------|
| JWT_SECRET   | Secret key for signing JWT tokens       | fallbackSecretKeyThatIsSecureAndItIsLeast256BitsLongForSure |
| SPRING_PROFILES_ACTIVE | Activate Spring profile        | (none)                                                  |

If you set `JWT_SECRET` in your environment it will override the fallback. Otherwise the fallback is used—fine for this demo.

Set `JWT_SECRET` (optional) examples:

Windows (cmd):
```bat
set JWT_SECRET=SomeValue
mvnw.cmd spring-boot:run
```
Linux / macOS:
```bash
export JWT_SECRET=SomeValue
./mvnw spring-boot:run
```

## API Documentation
OpenAPI (Swagger) docs are generated automatically.

### Run the Application
```bash
# Unix-like (Git Bash / WSL / macOS / Linux)
./mvnw spring-boot:run

# Windows cmd
mvnw.cmd spring-boot:run
```
App URL: `http://localhost:8080`

### Documentation Endpoints
| Purpose       | URL                                         |
|---------------|---------------------------------------------|
| Swagger UI    | http://localhost:8080/swagger-ui.html       |
| OpenAPI JSON  | http://localhost:8080/v3/api-docs           |
| OpenAPI YAML  | http://localhost:8080/v3/api-docs.yaml      |

### Security / Authorization (Quick Summary)
- Obtain a token via `/api/auth/login` (or register first)
- Use `Authorization: Bearer <token>` on secured endpoints
- In Swagger UI click Authorize and paste the token

### Example Auth Flow
Login request:
```json
POST /api/auth/login
{
  "usernameOrEmail": "john_doe",
  "password": "Str0ngP@ss!"
}
```
Example response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTc1OTg0ODE0NCwiZXhwIjoxNzU5OTM0NTQ0fQ.bcQ7Xr1j6-C-0dFHlxo6YMwQDiuzs7SvW6GtwRchw-I",
  "type": "Bearer",
  "username": "john_doe",
  "email": "john_doe@example.com",
  "expiresIn": 86400
}
```
Header usage:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Sample cURL
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"john_doe","password":"Str0ngP@ss!"}' | jq -r .token)

curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/users/me
```

## Quick Start (TL;DR)
```bash
mvnw.cmd spring-boot:run   # Windows
./mvnw spring-boot:run     # Linux/macOS
```

## Testing
```bash
mvnw.cmd test        # Windows
./mvnw test          # Linux/macOS
```

## Build & Run Jar
```bash
mvnw.cmd -DskipTests package
java -jar target/personal-finance-tracker-*.jar
```

### Generate a Client SDK (optional)
```bash
openapi-generator generate \
  -i http://localhost:8080/v3/api-docs \
  -g typescript-fetch \
  -o ./generated-client
```

## Roadmap

### Phase 1: Core Enhancements (In Progress)
- [ ] Enhanced error handling with custom exceptions
- [ ] Expanded validation logic for DTOs and entities
- [X] API documentation with Swagger/OpenAPI
- [ ] Additional unit test coverage for edge cases
- [ ] Pagination and sorting consistency review
- [ ] Connect to a real PostgreSQL database (replace in-memory H2 for persistence)

### Phase 2: Quality & Security
- [ ] Integration tests (repositories + security flow)
- [ ] Security hardening (rate limiting, basic input sanitization)
- [ ] Docker containerization (App + DB)
- [ ] Environment profiles (dev, test, demo)
- [ ] Structured logging (JSON option) & basic metrics

### Phase 3: Frontend Development
- [ ] Lightweight SPA (React/Vue) for core flows
- [ ] Dashboard: recent transactions + budget usage widgets
- [ ] Transaction CRUD UI with filtering
- [ ] Category management UI with merge capability
- [ ] Budget creation & progress visualization (charts)

### Phase 4: Advanced & AI-Powered Features
- [ ] Spending pattern clustering
- [ ] Budget recommendations based on historical averages
- [ ] Predictive future expense trend line
- [ ] Anomaly detection (outlier spend alerts)
- [ ] Smart auto-categorization suggestions
- [ ] CSV/Excel import with field mapping

### Phase 5: Polish & Production Readiness (If Ever Needed)
- [ ] Performance profiling & query optimization
- [ ] Full API specification refinement & examples
- [ ] Deployment guide (Docker Compose / optional cloud)
- [ ] Notification hooks (email / webhook stubs)
- [ ] Multi-currency planning (FX rate abstraction layer)

### Stretch Ideas (Nice-to-Have)
- [ ] Tagging system (tags in addition to categories)
- [ ] Recurring transaction scheduler
- [ ] Savings goals & progress tracking
- [ ] Export to PDF/CSV reports
- [ ] Multi-user shared budgets (household mode)

---

(Phases give recruiters a view of planned evolution; scope intentionally broad for demonstration.)
