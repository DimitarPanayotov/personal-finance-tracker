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
- **High Test Coverage**: 400+ tests with 93-94% coverage using JUnit5, AssertJ, and Spring Boot Test

## Technology Stack
- **Backend**: Spring Boot, Spring Security, Spring Data JPA
- **Database**: PostgreSQL (production), H2 (testing)
- **Authentication**: JWT (JSON Web Tokens)
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

## Roadmap

### Phase 1: Core Enhancements (In Progress)
- [ ] Enhanced error handling with custom exceptions
- [ ] Expanded validation logic
- [ ] API documentation with Swagger/OpenAPI
- [ ] Additional unit test coverage

### Phase 2: Quality & Security
- [ ] Integration tests for end-to-end workflows
- [ ] Security enhancements (rate limiting, input sanitization)
- [ ] Docker containerization
- [ ] Environment profiles (dev, staging, production)
- [ ] Structured logging and monitoring

### Phase 3: Frontend Development
- [ ] Basic frontend interface (React/Angular/Vue)
- [ ] User dashboard with financial visualizations
- [ ] Transaction and budget management UI

### Phase 4: AI-Powered Features
- [ ] Spending pattern analysis
- [ ] Budget recommendations based on historical data
- [ ] Future expense predictions
- [ ] Anomaly detection for unusual transactions
- [ ] Smart category suggestions

### Phase 5: Polish & Production Ready
- [ ] Performance optimization
- [ ] Complete API documentation
- [ ] Deployment guides
- [ ] Additional features based on feedback