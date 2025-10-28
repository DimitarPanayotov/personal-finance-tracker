package com.dimitar.financetracker.integration;

import com.dimitar.financetracker.dto.request.user.UserLoginRequest;
import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.response.user.AuthenticationResponse;
import com.dimitar.financetracker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully register a new user and return auth token")
    void testUserRegistration() throws Exception {
        UserRegistrationRequest registerRequest = new UserRegistrationRequest(
                "john_doe",
                "john@example.com",
                "SecurePass123!"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.expiresIn").isNumber());

        assertThat(userRepository.findByUsername("john_doe")).isPresent();
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should login with valid credentials and return JWT token")
    void testUserLogin() throws Exception {
        UserRegistrationRequest registerRequest = new UserRegistrationRequest(
                "jane_doe",
                "jane@example.com",
                "MyPassword456!"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        UserLoginRequest loginRequest = new UserLoginRequest(
                "jane_doe",
                "MyPassword456!"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("jane_doe"));
    }

    @Test
    @DisplayName("Should reject login with invalid credentials")
    void testLoginWithInvalidCredentials() throws Exception {
        UserRegistrationRequest registerRequest = new UserRegistrationRequest(
                "bob_smith",
                "bob@example.com",
                "CorrectPassword789!"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        UserLoginRequest wrongPasswordRequest = new UserLoginRequest(
                "bob_smith",
                "WrongPassword999!"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Complete flow: Register → Login → Access protected endpoint")
    void testCompleteAuthenticationFlow() throws Exception {
        UserRegistrationRequest registerRequest = new UserRegistrationRequest(
                "alice_wonder",
                "alice@example.com",
                "AlicePass123!"
        );

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        AuthenticationResponse authResponse = objectMapper.readValue(registerResponseBody, AuthenticationResponse.class);
        String jwtToken = authResponse.getToken();

        assertThat(jwtToken).isNotEmpty();

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice_wonder"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should reject registration with duplicate username")
    void testDuplicateUsernameRegistration() throws Exception {
        UserRegistrationRequest firstUser = new UserRegistrationRequest(
                "duplicate_user",
                "first@example.com",
                "Password123!"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstUser)))
                .andExpect(status().isOk());

        UserRegistrationRequest duplicateUser = new UserRegistrationRequest(
                "duplicate_user",
                "second@example.com",
                "DifferentPass456!"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should reject registration with invalid email format")
    void testInvalidEmailRegistration() throws Exception {
        UserRegistrationRequest invalidEmailRequest = new UserRegistrationRequest(
                "test_user",
                "not-an-email",
                "ValidPass123!"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                .andExpect(status().isBadRequest());
    }
}
