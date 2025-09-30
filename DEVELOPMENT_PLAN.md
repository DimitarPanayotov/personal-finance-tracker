# Development Plan

## Day 1 Progress
- Created Spring Boot project
- Set up Git repository
- Verified application runs

## Day 2 Progress
- Made a Database schema
- Made the project structure
- Made the first entity(User),
had some problems with H2 Database and added a SecurityConfig file to 
fix them(not completely understanding how they work, 
will come later when we start the security implementation)
- Test the User entity in a DB with H2 Database - works properly

## Day 3 Progress
- Added the Category and Transaction entities and tested with H2 database
- Added validation in Jakarta Validation in the entity classes
- Added a new entity: Budget (in the DB schema and in the entity package)
- Added repositories for User, Category, Transaction and Budget
- Added PostgreSQL database, still testing on h2 though
- Worked on the project structure and started making DTOs
- Working on CQRS pattern
- Working on validation with annotations and ErrorMessages class
- Made the User DTO - request, response and mapper

## Day 4 Progress
- Wrote tests for the User DTO with JUnit5, AssertJ, Jakarta Bean Validation
- Wrote tests for the Repositories with JPATest
- Wrote tests for the entities
- Made the Category DTOs + tests
- Ran all tests and passed with 93-94% coverage

## Day 5 Progress
- Made Transaction DTOs + mapper + tests
- 350 tests all passing with high coverage
- Made Budget DTOs + mapper + test
- 400 tests all passing with high coverage
- DTOs FINISHED!

## Day 6 Progress
- Did research about the Service layer and controllers
- Created Query and Command interfaces (CQRS pattern)
- Created first service - Create User
- Created the controller and tested in Postman - everything is ok!
- Created global exception handler + error responses

## Day 7 Progress
- Created Get User by id query request
- Tested with Postman and added custom Exception, updated the Exception Handler
- Created Update User request and Delete User Request (basic CRUD user operations finished)
- Created Requests for Creating Category, Getting Category by id and all categories by user id
- Testes everything with Postman and the relation are OK!

## Day 8 Progress
- Created Update Category Service and tested in Postman
- Created Delete Category Service and tested in Postman
- Added basic JWT Security
- JwtUtil: Creates and validates tokens
  AuthenticationService: Business logic for register/login
- CustomUserDetailsService: Loads user for Spring Security
- JwtAuthenticationFilter: Validates tokens on every request
- SecurityConfig: Configures which endpoints need authentication
- Stateless system - the client proves identity with each request using the jwt token
- The flow is - login or register to get a JWT token, use this token in authorization header with Bearer TOKEN - access the protected endpoints

## Day 9 Progress
- Worked on security
- Updated the User and Category controllers, so they work with JWT tokens instead of @RequestParam ID
- Now user registers or logs in -> sees categories or updates something without a need of his id
- Tested everything in postman
- Started with Transaction Service, implemented Create Transaction, tested it
- Implemented Get All Transactions
- Implemented Create Budget and Get All Budgets to test - everything works
- Implemented Password Change for User
- Fixed Get Category by id bug

## Day 10 Progress 
- Read in depth about security, made a helper file
- Changed the Service layer by removing input records and moved 
the authentication logic from the controller to the Service layer
- Added User Statistics, Merge categories, Import default categories
- Added Search Category by name, Update Transaction, Delete Transaction, Duplicate Transaction,
Get Transaction by ID,  Get Transactions in Date Range, Get Transaction By category,
Get Transactions in Amount Range, Search Transaction

## Day 11 Progress
- Added Update Budget, Delete Budget, Activate and Deactivate Budget
- Added Get Budget by Id, Get Budget by Category
- Added Get Budget Usage and All Budgets Usage(connected to the transactions)
- Added Get Active Budgets
- Tested in Postman
- Basic Service Layer Finished!

# Immediate next steps
- Service-layer tests: Add unit tests for your new commands/queries (activate/deactivate/delete/get by id/by category/get active). Use Mockito for AuthenticationFacade and repositories.
- Business rules on budgets: Enforce “no overlapping active budgets for the same category and time window” in create/update; surface a clear error when it happens.
- Pagination and filtering: Add pageable/sort to list endpoints (transactions, budgets, categories) to keep responses fast as data grows.
- API docs: Add OpenAPI/Swagger UI to document endpoints and enable easy testing.
- Database migrations: Introduce Flyway or Liquibase and move off ddl-auto=create-drop for non-dev profiles.

## Next Steps
- Adding more functionality to the service layer
- User commands: account deactivation, reset account
- User queries: get user activity summary, get user preferences, validate user status
- Category commands: archive, restore
- Category queries: get active, get popular, get empty, get summary, get by date range
- Transaction commands: 
- Transaction queries: Summary(spending by category, period, etc.) -> should move to user, GetMonthly/WeeklySpending -> to user, GetTopSpending Categories -> to user, GetRecurringTransactions
- Budget commands: Reset, Bulk Crate Budgets(multiple budgets at once - yearly budgets broken down by month)
- Budget queries: BudgetAlerts(close to being spent), BudgetSummary
- Advanced Features: Financial Dashboard, Monthly Financial Summary, Goal Progress Tracking
- Smart Features: Budget Recommendations, Spending Alerts, Anomaly Detection, Smart Categorization
- Adding Validation and Error Handling
- Testing

## Ideas
- After Service Layer: Enhanced Error Handling - Custom exceptions, validation
  Comprehensive Testing - Unit tests for your new services
  API Documentation - Swagger/OpenAPI docs
- Then: Integration Tests - End-to-end testing
  Security Enhancements - Rate limiting, input sanitization
  Deployment Preparation - Docker, profiles, logging
- Then: Later (Polish Phase)

## Short-term roadmap
- Testing and quality gates
Unit tests: Services, mappers, exception cases. Aim ≥80% for service package.
Integration tests: A couple of end-to-end flows (auth -> create category -> create budget -> query active budgets).
Test data builders/fixtures for clean tests.
- Security hardening
  Use env-based JWT secret and expiry per profile, disable H2 console in prod.
  Optional: refresh tokens, token blacklist on password change, basic rate limiting.
  Roles/authorities if you foresee admin vs user in the future.
- Data and migrations
  Flyway baseline with initial schema; add constraints and indexes (user_id, category_id, transaction_date).
  Unique constraints on username/email; foreign keys checked; cascade strategy reviewed.
- API ergonomics
  Pagination/sorting on GET endpoints.
  Consistent error format; tighten validation messages.
  Add “currently active by date” filter (e.g., /api/budgets/active?on=2025-09-30) in addition to isActive.
- Observability and logging
  Structured logging with request IDs, audit key actions (login, create/update/delete).
  Basic metrics/health endpoints (Spring Boot Actuator).
- DevOps
  Dockerize app + Postgres; dev/prod profiles.
  CI/CD (GitHub Actions): build, test, run unit/integration tests, optionally publish Docker image.
  Dependabot or Renovate for dependency updates.
- Documentation
  Swagger UI (springdoc) exposed in dev.
  Update README with run instructions, env vars, Postman collection, and example curl calls.
  Keep DEVELOPMENT_PLAN.md in sync with what’s shipped.



