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

## Next Steps
- Fixing the controllers by removing input records and adding the logic with the authenticated user to the services instead of the controllers
- Adding more functionality to the services
- GET /api/users/me/statistics
- User commands: account deactivation, reset account
- User queries: Get user statistics, get user activity summary, get user preferences, validate user status
- Category commands: merge categories, archive, restore, import default categories
- Category queries: get by type, get active, get popular, get empty, search, get summary, get by date range
- Transaction commands: Update, Delete, Duplicate
- Transaction queries: GetById, GetInDateRange, GetByCategory, GetByAmountRange, Search(By description or keywords), Summary(spending by category, period, etc.), GetMonthly/WeeklySpending, GetTopSpending Categories, GetRecentTransactions, GetRecurringTransactions
- Budget commands: Update, Delete, Deactivate, Reset, Bulk Crate Budgets(multiple budgets at once - yearly budgets broken down by month)
- Budget queries: GetById, GetByCategory, GetActiveBudgets, GetBudgetUsage(how much of each budget has been spent), BudgetAlerts(close to being spent), BudgetSummary
- Advanced Features: Financial Dashboard, Monthly Financial Summary, Goal Progress Tracking
- Smart Features: Budget Recommendations, Spending Alerts, Anomaly Detection, Smart Categorization
- Starting security
- Read a lot about security
- Adding Validation and Error Handling
- Testing
- Services and controllers - make query and command interfaces that should be implemented by the services
- Security with JWT authentication

## Ideas
- After Service Layer: Enhanced Error Handling - Custom exceptions, validation
  Comprehensive Testing - Unit tests for your new services
  API Documentation - Swagger/OpenAPI docs
- Then: Integration Tests - End-to-end testing
  Security Enhancements - Rate limiting, input sanitization
  Deployment Preparation - Docker, profiles, logging
- Then: Later (Polish Phase):
  Integration Tests - End-to-end testing
  Security Enhancements - Rate limiting, input sanitization
  Deployment Preparation - Docker, profiles, logging


