# 🔐 Spring Security Flow in FinanceTracker

This document explains the authentication & authorization flow in the **FinanceTracker** project.

---

## 1. **Registration Flow**
1. Client sends `POST /api/auth/register` with:
    - `username`
    - `email`
    - `password`
2. **`AuthenticationService.register()`**:
    - Checks if username/email already exist.
    - Encodes password using **BCrypt**.
    - Saves user in DB (`UserRepository`).
    - Generates **JWT token** via `JwtUtil`.
    - Returns `AuthenticationResponse`:
      ```json
      {
        "token": "JWT_TOKEN_HERE",
        "type": "Bearer",
        "username": "...",
        "email": "...",
        "expiresIn": 86400000
      }
      ```

---

## 2. **Login Flow**
1. Client sends `POST /api/auth/login` with:
    - `usernameOrEmail`
    - `password`
2. **`AuthenticationService.login()`**:
    - Authenticates credentials via `AuthenticationManager` + `DaoAuthenticationProvider`.
    - Loads `UserDetails` via **CustomUserDetailsService**.
    - Generates **JWT token** via `JwtUtil`.
    - Fetches user info from DB.
    - Returns `AuthenticationResponse`.

---

## 3. **JWT Utility (`JwtUtil`)**
- **Generates tokens**:
    - Subject = username
    - Expiration = configurable (default 24h)
    - Signed with Base64 secret key (HS256)
- **Validates tokens**:
    - Extracts claims (subject, expiration)
    - Compares username
    - Checks expiration

---

## 4. **Authentication Filter (`JwtAuthenticationFilter`)**
- Runs **once per request** (`OncePerRequestFilter`).
- Steps:
    1. Reads `Authorization` header.
    2. Extracts token if header starts with `Bearer `.
    3. Uses `JwtUtil` to extract username.
    4. Loads user with **CustomUserDetailsService**.
    5. Validates token.
    6. If valid:
        - Creates `UsernamePasswordAuthenticationToken`.
        - Stores it in `SecurityContextHolder`.
    7. Passes request along filter chain.

---

## 5. **Security Configuration (`SecurityConfig`)**
- Configures Spring Security:
    - **CSRF disabled** (API is stateless).
    - `SessionCreationPolicy.STATELESS`.
    - Public endpoints:
        - `/api/auth/**`
        - `/h2-console/**`
    - Secured endpoints:
        - `/api/**` → requires authentication.
- Registers:
    - `PasswordEncoder` (BCrypt).
    - `AuthenticationProvider` (DAO with UserDetailsService).
    - `AuthenticationManager`.
    - Adds `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`.

---

## 6. **Accessing the Authenticated User**
- **`AuthenticationFacade`**:
    - Fetches current `Authentication` from `SecurityContextHolder`.
    - Retrieves username/email.
    - Loads full `User` entity from DB.
    - Provides helper: `getAuthenticatedUserId()`.

---

## 7. **Overall Request Lifecycle**
1. **User registers/logs in** → gets JWT.
2. **Client sends request** with header:
3. **JwtAuthenticationFilter** intercepts:
- Validates JWT.
- Sets authentication context.
4. **Controller/Service** can access authenticated user via:
- `SecurityContextHolder`
- `@AuthenticationPrincipal`
- `AuthenticationFacade`

---

✅ **Stateless, token-based authentication**  
✅ **Password hashing (BCrypt)**  
✅ **Custom user loading via DB**  
✅ **Fine-grained endpoint protection**


## 8. **Sequence Diagram**

```mermaid
sequenceDiagram
 participant Client
 participant AuthController
 participant AuthenticationService
 participant UserRepository
 participant JwtUtil
 participant SecurityFilterChain
 participant JwtAuthenticationFilter
 participant Controller

 Client->>AuthController: POST /api/auth/register (username, email, password)
 AuthController->>AuthenticationService: register()
 AuthenticationService->>UserRepository: check username/email exists
 AuthenticationService->>UserRepository: save new user
 AuthenticationService->>JwtUtil: generateToken(userDetails)
 JwtUtil-->>AuthenticationService: JWT token
 AuthenticationService-->>Client: AuthenticationResponse (token, user info)

 Client->>AuthController: POST /api/auth/login (username/email, password)
 AuthController->>AuthenticationService: login()
 AuthenticationService->>AuthenticationManager: authenticate(credentials)
 AuthenticationService->>JwtUtil: generateToken(userDetails)
 AuthenticationService->>UserRepository: fetch user entity
 AuthenticationService-->>Client: AuthenticationResponse (token, user info)

 Client->>Controller: GET /api/secure-data (Authorization: Bearer JWT)
 Controller->>SecurityFilterChain: request enters filter chain
 SecurityFilterChain->>JwtAuthenticationFilter: process request
 JwtAuthenticationFilter->>JwtUtil: extract username
 JwtAuthenticationFilter->>UserRepository: load user
 JwtAuthenticationFilter->>JwtUtil: validate token
 JwtAuthenticationFilter->>SecurityContextHolder: set authentication
 JwtAuthenticationFilter-->>Controller: continue request
 Controller-->>Client: secured response