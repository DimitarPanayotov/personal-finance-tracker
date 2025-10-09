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

## API Features
- User authentication (register/login) with JWT tokens
- Category operations with search and merge capabilities
- Advanced transaction filtering and management
- Budget tracking with usage analytics
- Password management

## Quick Start (Recommended: Docker)

### Prerequisites
- **Docker Desktop** installed ([download here](https://www.docker.com/products/docker-desktop))

### Run with Docker Compose (Zero Configuration)

```bash
# Clone the repository
git clone https://github.com/DimitarPanayotov/personal-finance-tracker.git
cd personal-finance-tracker

# Start everything with one command
docker-compose up --build
```

The application will:
- Start PostgreSQL database with demo credentials
- Build and start the Spring Boot application
- Start pgAdmin for database management
- Application available at `http://localhost:8080`
- Swagger UI at `http://localhost:8080/swagger-ui.html`
- pgAdmin at `http://localhost:5050`

### Access Points

| Service | URL | Purpose |
|---------|-----|---------|
| **Application API** | `http://localhost:8080` | REST API endpoints |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` | Interactive API documentation |
| **pgAdmin** | `http://localhost:5050` | Database management interface |

### pgAdmin Database Access

To view and manage the PostgreSQL database using pgAdmin:

1. **Open pgAdmin**: Navigate to `http://localhost:5050`

2. **Login to pgAdmin:**
   - Email: `admin@admin.com`
   - Password: `admin`

3. **Register Database Server:**
   - Right-click **"Servers"** → **"Register"** → **"Server"**
   
   **General Tab:**
   - Name: `Finance Tracker DB` (or any name)
   
   **Connection Tab:**
   - Host: `postgres`
   - Port: `5432`
   - Database: `personal-finance-tracker`
   - Username: `personal_finance_tracker_user`
   - Password: `demo_password_123`
   
4. **Browse Data:**
   - Navigate: **Servers** → **Finance Tracker DB** → **Databases** → **personal-finance-tracker** → **Schemas** → **public** → **Tables**
   - Right-click any table → **"View/Edit Data"** → **"All Rows"**

### Stop the Application
```bash
# Stop containers (keeps data)
docker-compose down

# Stop and remove all data
docker-compose down -v
```

### Default Demo Credentials
- **Database Name**: `personal-finance-tracker`
- **Database User**: `personal_finance_tracker_user`
- **Database Password**: `demo_password_123`
- **JWT Secret**: Uses secure fallback from application properties
- **pgAdmin Email**: `admin@admin.com`
- **pgAdmin Password**: `admin`

---

## Alternative: Run Locally (Without Docker)

### Prerequisites
- Java 21 or higher
- PostgreSQL 16 installed and running
- Maven (or use included Maven wrapper)

### Database Setup
1. Create PostgreSQL database:
```sql
CREATE DATABASE personal-finance-tracker;
CREATE USER personal_finance_tracker_user WITH PASSWORD 'demo_password_123';
GRANT ALL PRIVILEGES ON DATABASE personal-finance-tracker TO personal_finance_tracker_user;
```

2. Ensure PostgreSQL is running on `localhost:5432`

### Run the Application
```bash
# Windows (cmd)
mvnw.cmd spring-boot:run

# Linux / macOS / Git Bash
./mvnw spring-boot:run
```

Application will be available at: `http://localhost:8080`

---

## Configuration

### Environment Variables (Optional)

The application works out-of-the-box with sensible defaults. For production or custom setups, you can override:

| Variable                    | Purpose                          | Default Value                                               |
|-----------------------------|----------------------------------|-------------------------------------------------------------|
| `POSTGRES_PASSWORD`         | Database password                | `demo_password_123`                                         |
| `JWT_SECRET`                | JWT token signing key            | `fallbackSecretKeyThatIsSecureAndItIsLeast256BitsLongForSure` |
| `SPRING_DATASOURCE_URL`     | Database connection URL          | `jdbc:postgresql://localhost:5432/personal-finance-tracker` |
| `SPRING_DATASOURCE_USERNAME`| Database username                | `personal_finance_tracker_user`                             |

### Custom Configuration

**With Docker Compose**: Create `.env` file in project root:
```properties
POSTGRES_PASSWORD=your_secure_password
JWT_SECRET=your_jwt_secret_at_least_256_bits
```

**Without Docker** (Windows cmd):
```bat
set JWT_SECRET=YourCustomSecret
set SPRING_DATASOURCE_PASSWORD=YourPassword
mvnw.cmd spring-boot:run
```

**Without Docker** (Linux/macOS):
```bash
export JWT_SECRET=YourCustomSecret
export SPRING_DATASOURCE_PASSWORD=YourPassword
./mvnw spring-boot:run
```

---

## API Documentation
OpenAPI (Swagger) docs are generated automatically.

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
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTc1OTg0ODE0NCwiZXhwIjoxNzU5OTM0NTQ0f...

### Sample cURL
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"john_doe","password":"Str0ngP@ss!"}' | jq -r .token)

curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/users/me
```

## Testing
```bash
# Windows
mvnw.cmd test

# Linux/macOS
./mvnw test
```

## Build & Run Jar
```bash
# Build
mvnw.cmd clean package -DskipTests      # Windows
./mvnw clean package -DskipTests        # Linux/macOS

# Run
java -jar target/personal-finance-tracker-0.0.1-SNAPSHOT.jar
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
- [X] Connect to a real PostgreSQL database (replace in-memory H2 for persistence)
- [X] Docker containerization (App + DB)

### Phase 2: Quality & Security
- [ ] Integration tests (repositories + security flow)
- [ ] Security hardening (rate limiting, basic input sanitization)
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
