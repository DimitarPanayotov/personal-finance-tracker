package com.dimitar.financetracker.controller;

import com.dimitar.financetracker.dto.request.user.UserLoginRequest;
import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.response.user.AuthenticationResponse;
import com.dimitar.financetracker.exception.GlobalExceptionHandler;
import com.dimitar.financetracker.exception.user.UserAlreadyExistsException;
import com.dimitar.financetracker.exception.user.UserDoesNotExistException;
import com.dimitar.financetracker.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(authenticationService);
        this.objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(objectMapper);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(jsonConverter)
                .setValidator(validator)
                .build();
    }

    @Nested
    class Register {
        @Test
        @DisplayName("POST /api/auth/register returns 200 with AuthenticationResponse on success")
        void register_success() throws Exception {
            AuthenticationResponse response = AuthenticationResponse.builder()
                    .token("jwt-token-123")
                    .type("Bearer")
                    .username("john")
                    .email("john@example.com")
                    .expiresIn(86400L)
                    .build();

            when(authenticationService.register(any(UserRegistrationRequest.class)))
                    .thenReturn(response);

            UserRegistrationRequest request = UserRegistrationRequest.builder()
                    .username("john")
                    .email("john@example.com")
                    .password("secret123")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.token", is("jwt-token-123")))
                    .andExpect(jsonPath("$.type", is("Bearer")))
                    .andExpect(jsonPath("$.username", is("john")))
                    .andExpect(jsonPath("$.email", is("john@example.com")))
                    .andExpect(jsonPath("$.expiresIn", is(86400)));
        }

        @Test
        @DisplayName("POST /api/auth/register returns 400 with validation errors when payload invalid")
        void register_validationErrors() throws Exception {
            // Blank payload triggers @NotBlank and @Email validations
            UserRegistrationRequest invalid = UserRegistrationRequest.builder()
                    .username("")
                    .email("not-an-email")
                    .password("")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")))
                    .andExpect(jsonPath("$.message", is("Validation failed for user registration")))
                    .andExpect(jsonPath("$.path", is("/api/auth/register")))
                    .andExpect(jsonPath("$.errors.username", anyOf(is("Username is required"), is("Username must be less than 50 characters"))))
                    .andExpect(jsonPath("$.errors.email", anyOf(is("Email must be valid"), is("Email is required"))))
                    .andExpect(jsonPath("$.errors.password", anyOf(is("Password is required"), is("Password must be at least 6 characters"))));
        }

        @Test
        @DisplayName("POST /api/auth/register returns 409 when user already exists")
        void register_duplicateUser() throws Exception {
            when(authenticationService.register(any(UserRegistrationRequest.class)))
                    .thenThrow(new UserAlreadyExistsException("Username already exists: john"));

            UserRegistrationRequest request = UserRegistrationRequest.builder()
                    .username("john")
                    .email("john@example.com")
                    .password("secret123")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status", is(409)))
                    .andExpect(jsonPath("$.error", is("Conflict")))
                    .andExpect(jsonPath("$.message", is("Username already exists: john")))
                    .andExpect(jsonPath("$.path", is("/api/auth/register")));
        }
    }

    @Nested
    class Login {
        @Test
        @DisplayName("POST /api/auth/login returns 200 with AuthenticationResponse on success")
        void login_success() throws Exception {
            AuthenticationResponse response = AuthenticationResponse.builder()
                    .token("jwt-abc")
                    .type("Bearer")
                    .username("jane")
                    .email("jane@example.com")
                    .expiresIn(86400L)
                    .build();

            when(authenticationService.login(any(UserLoginRequest.class)))
                    .thenReturn(response);

            UserLoginRequest request = UserLoginRequest.builder()
                    .usernameOrEmail("jane")
                    .password("p4ssw0rd")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.token", is("jwt-abc")))
                    .andExpect(jsonPath("$.type", is("Bearer")))
                    .andExpect(jsonPath("$.username", is("jane")))
                    .andExpect(jsonPath("$.email", is("jane@example.com")))
                    .andExpect(jsonPath("$.expiresIn", is(86400)));
        }

        @Test
        @DisplayName("POST /api/auth/login returns 400 with validation errors when payload invalid")
        void login_validationErrors() throws Exception {
            UserLoginRequest invalid = UserLoginRequest.builder()
                    .usernameOrEmail("")
                    .password("")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")))
                    .andExpect(jsonPath("$.message", is("Validation failed for user registration")))
                    .andExpect(jsonPath("$.path", is("/api/auth/login")))
                    .andExpect(jsonPath("$.errors.usernameOrEmail", is("Username or email required")))
                    .andExpect(jsonPath("$.errors.password", is("Password is required")));
        }

        @Test
        @DisplayName("POST /api/auth/login returns 404 when user not found")
        void login_userNotFound() throws Exception {
            when(authenticationService.login(any(UserLoginRequest.class)))
                    .thenThrow(new UserDoesNotExistException("User not found"));

            UserLoginRequest request = UserLoginRequest.builder()
                    .usernameOrEmail("ghost")
                    .password("whatever")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("User not found")))
                    .andExpect(jsonPath("$.path", is("/api/auth/login")));
        }
    }
}
