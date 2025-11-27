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
- **Deployment**: Docker & Docker Compose

## Project Structure
- **Entities**: User, Category, Transaction, Budget
- **DTOs**: Request/Response patterns with dedicated mappers
- **Services**: Command and Query separation following CQRS
- **Controllers**: RESTful endpoints with comprehensive error handling
- **Security**: Custom authentication filter and user details service

## Database Schema
**Live Diagram:** [View on dbdiagram.io](https://dbdiagram.io/d/PersonalFinanceTrackerDiagram-68aaf8b21e7a611967557bf8)

## Quick Start (Docker)

### 1. Prerequisites
- **Docker Desktop** installed.

### 2. Run (Zero Configuration)
Clone the repo, navigate into the directory, and run:
```bash
# Clone, enter directory, and start (includes build)
git clone https://github.com/DimitarPanayotov/personal-finance-tracker.git
cd personal-finance-tracker
docker-compose up --build
```

### 3. Access Points
| Service | URL |
|---|---|
| Application API | http://localhost:8080 |
| Swagger UI (Docs) | http://localhost:8080/swagger-ui.html |
| pgAdmin (Database) | http://localhost:5050 |

### 4. pgAdmin Database Access
1.  **Open pgAdmin**: `http://localhost:5050`
2.  **Login**:
    *   Email: `admin@admin.com`
    *   Password: `admin`
3.  **Register Server** (Right-click Servers > Register > Server):
    *   **General** > Name: `Finance Tracker DB` (or any name)
    *   **Connection** > Host: `postgres`
    *   **Connection** > Database: `personal-finance-tracker`
    *   **Connection** > Username: `personal_finance_tracker_user`
    *   **Connection** > Password: `demo_password_123`

### 5. Stop the Application
```bash
# Stop containers (keeps data)
docker-compose down

# Stop and remove all data
docker-compose down -v
```

### Default Credentials
- **Database Name**: `personal-finance-tracker`
- **Database User**: `personal_finance_tracker_user`
- **Database Password**: `demo_password_123`
- **JWT Secret**: Uses secure fallback from application properties
- **pgAdmin Email**: `admin@admin.com`
- **pgAdmin Password**: `admin`

---

## Local Development (Without Docker)

### 1. Prerequisites
- Java 21+
- PostgreSQL 16+
- Maven (or use the included wrapper)

### 2. Setup & Run
1.  **Create Database**: In PostgreSQL, create a database named `personal-finance-tracker` and a user `personal_finance_tracker_user` with password `demo_password_123`, then grant it all privileges.
2.  **Run Application**:
    ```bash
    # Use mvnw.cmd on Windows
    ./mvnw spring-boot:run
    ```
    The application will be available at `http://localhost:8080`.

---

## Development

### Configuration
The application is pre-configured but can be customized with environment variables (e.g., `JWT_SECRET`, `POSTGRES_PASSWORD`).

**Example (Linux/macOS):**
```bash
export JWT_SECRET=your_secure_secret
./mvnw spring-boot:run
```

### API Documentation
Interactive API documentation is available at the `/swagger-ui.html` endpoint.
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

To authorize, get a token from the `/api/auth/login` endpoint and use it in Swagger's "Authorize" dialog (`Bearer <token>`).

### Testing & Building
```bash
# Run tests
./mvnw test

# Build the JAR
./mvnw clean package -DskipTests

# Run the JAR
java -jar target/personal-finance-tracker-0.0.1-SNAPSHOT.jar
```


## Roadmap

### Phase 1: Core Enhancements (In Progress)
- [X] Enhanced error handling with custom exceptions
- [X] Business rule enforcement (budget overlap prevention)
- [X] API documentation with Swagger/OpenAPI
- [X] Additional unit test coverage for edge cases
- [X] Pagination and sorting for list endpoints
- [X] Connect to a real PostgreSQL database (replace in-memory H2 for persistence)
- [X] Docker containerization (App + DB)

### Phase 2: Quality & Security
- [X] Integration tests (repositories + security flow)
- [X] Security hardening (rate limiting)
- [ ] Environment profiles (dev, test, demo)
- [ ] Structured logging (JSON option) & basic metrics

### Phase 3: Frontend Development
- [X] Lightweight SPA (React/Vue) for core flows
- [X] Dashboard: recent transactions + budget usage widgets
- [X] Transaction CRUD UI with filtering
- [ ] Category management UI with merge capability
- [X] Budget creation & progress visualization (charts)

### Phase 4: Advanced & AI-Powered Features
- [ ] Spending pattern clustering
- [ ] Budget recommendations based on historical averages
- [ ] Predictive future expense trend line
- [ ] Anomaly detection (outlier spend alerts)
- [ ] Smart auto-categorization suggestions
- [ ] CSV/Excel import with field mapping

### Phase 5: Polish & Production Readiness
- [ ] Performance profiling & query optimization
- [ ] Full API specification refinement & examples
- [ ] Notification hooks (email / webhook stubs)
- [ ] Multi-currency planning (FX rate abstraction layer)

### Stretch Ideas
- [ ] Tagging system (tags in addition to categories)
- [ ] Recurring transaction scheduler
- [ ] Savings goals & progress tracking
- [ ] Export to PDF/CSV reports
- [ ] Multi-user shared budgets (household mode)

---