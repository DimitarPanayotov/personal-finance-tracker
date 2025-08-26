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

## Next Steps
- Finish DTOs
- Services and controllers - make query and command interfaces that should be implemented by the services
- Security with JWT authentication

## Ideas

