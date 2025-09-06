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
- Created Get User by Id query request
- Tested with Postman and added custom Exception, updated the Exception Handler
- Created Update User request and Delete User Request (basic CRUD user operations finished)
- Created Requests for Creating Category, Getting Category by Id and all categories by user id
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

## Next Steps
- Starting security
- Read a lot about security
- Adding Validation and Error Handling
- Testing
- Services and controllers - make query and command interfaces that should be implemented by the services
- Security with JWT authentication

## Ideas

