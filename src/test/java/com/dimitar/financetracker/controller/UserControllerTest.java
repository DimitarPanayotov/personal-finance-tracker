package com.dimitar.financetracker.controller;

import com.dimitar.financetracker.dto.request.user.PasswordChangeRequest;
import com.dimitar.financetracker.dto.request.user.UserUpdateRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.dto.response.user.UserStatisticsResponse;
import com.dimitar.financetracker.exception.GlobalExceptionHandler;
import com.dimitar.financetracker.exception.user.DuplicateEmailException;
import com.dimitar.financetracker.exception.user.DuplicateUsernameException;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock com.dimitar.financetracker.service.UserService userService;

    @BeforeEach
    void setUp() {
        UserController controller = new UserController(userService);
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
    class UpdateCurrentUser {
        @Test
        @DisplayName("PATCH /api/users/me returns 200 with UserResponse on success")
        void update_success() throws Exception {
            UserResponse response = UserResponse.builder()
                    .id(1L)
                    .username("newname")
                    .email("new@example.com")
                    .createdAt(LocalDateTime.now().minusDays(5))
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(userService.updateUser(any(UserUpdateRequest.class))).thenReturn(response);

            UserUpdateRequest request = UserUpdateRequest.builder()
                    .username("newname")
                    .email("new@example.com")
                    .build();

            mockMvc.perform(patch("/api/users/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.username", is("newname")))
                    .andExpect(jsonPath("$.email", is("new@example.com")));
        }

        @Test
        @DisplayName("PATCH /api/users/me returns 400 with validation errors when payload invalid")
        void update_validationErrors() throws Exception {
            String longUsername = "x".repeat(51); // > 50
            UserUpdateRequest invalid = UserUpdateRequest.builder()
                    .username(longUsername)
                    .email("invalid-email")
                    .build();

            mockMvc.perform(patch("/api/users/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")))
                    .andExpect(jsonPath("$.message", is("Validation failed for user registration")))
                    .andExpect(jsonPath("$.path", is("/api/users/me")))
                    .andExpect(jsonPath("$.errors.username", is("Username must be less than 50 characters")))
                    .andExpect(jsonPath("$.errors.email", is("Email must be valid")));
        }

        @Test
        @DisplayName("PATCH /api/users/me returns 409 when username already exists")
        void update_conflict_username() throws Exception {
            when(userService.updateUser(any(UserUpdateRequest.class)))
                    .thenThrow(new DuplicateUsernameException("Username already exists"));

            UserUpdateRequest request = UserUpdateRequest.builder()
                    .username("taken")
                    .build();

            mockMvc.perform(patch("/api/users/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status", is(409)))
                    .andExpect(jsonPath("$.error", is("Conflict")))
                    .andExpect(jsonPath("$.message", is("Username already exists")))
                    .andExpect(jsonPath("$.path", is("/api/users/me")));
        }

        @Test
        @DisplayName("PATCH /api/users/me returns 409 when email already exists")
        void update_conflict_email() throws Exception {
            when(userService.updateUser(any(UserUpdateRequest.class)))
                    .thenThrow(new DuplicateEmailException("Email already exists"));

            UserUpdateRequest request = UserUpdateRequest.builder()
                    .email("exists@example.com")
                    .build();

            mockMvc.perform(patch("/api/users/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status", is(409)))
                    .andExpect(jsonPath("$.error", is("Conflict")))
                    .andExpect(jsonPath("$.message", is("Email already exists")))
                    .andExpect(jsonPath("$.path", is("/api/users/me")));
        }
    }

    @Nested
    class ChangePassword {
        @Test
        @DisplayName("PATCH /api/users/me/change-password returns 200 on success")
        void changePassword_success() throws Exception {
            UserResponse response = UserResponse.builder()
                    .id(1L)
                    .username("john")
                    .email("john@example.com")
                    .createdAt(LocalDateTime.now().minusDays(10))
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(userService.changePassword(any(PasswordChangeRequest.class)))
                    .thenReturn(response);

            PasswordChangeRequest request = PasswordChangeRequest.builder()
                    .password("oldSecret")
                    .newPassword("newSecret123")
                    .build();

            mockMvc.perform(patch("/api/users/me/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.username", is("john")))
                    .andExpect(jsonPath("$.email", is("john@example.com")));
        }

        @Test
        @DisplayName("PATCH /api/users/me/change-password returns 400 with validation errors when payload invalid")
        void changePassword_validationErrors_blank() throws Exception {
            PasswordChangeRequest invalid = PasswordChangeRequest.builder()
                    .password("")
                    .newPassword("")
                    .build();

            mockMvc.perform(patch("/api/users/me/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")))
                    .andExpect(jsonPath("$.message", is("Validation failed for user registration")))
                    .andExpect(jsonPath("$.path", is("/api/users/me/change-password")))
                    .andExpect(jsonPath("$.errors.password", is("Current password required")))
                    .andExpect(jsonPath("$.errors.newPassword", anyOf(is("Password is required"), is("Password must be at least 6 characters"))));
        }

        @Test
        @DisplayName("PATCH /api/users/me/change-password returns 400 when newPassword too short")
        void changePassword_validationErrors_tooShort() throws Exception {
            PasswordChangeRequest invalid = PasswordChangeRequest.builder()
                    .password("old")
                    .newPassword("123")
                    .build();

            mockMvc.perform(patch("/api/users/me/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")))
                    .andExpect(jsonPath("$.message", is("Validation failed for user registration")))
                    .andExpect(jsonPath("$.path", is("/api/users/me/change-password")))
                    .andExpect(jsonPath("$.errors.newPassword", is("Password must be at least 6 characters")));
        }

        @Test
        @DisplayName("PATCH /api/users/me/change-password returns 400 Bad Request on illegal argument (e.g., wrong current password)")
        void changePassword_illegalArgument() throws Exception {
            when(userService.changePassword(any(PasswordChangeRequest.class)))
                    .thenThrow(new IllegalArgumentException("Current password is incorrect"));

            PasswordChangeRequest request = PasswordChangeRequest.builder()
                    .password("wrong")
                    .newPassword("newSecret123")
                    .build();

            mockMvc.perform(patch("/api/users/me/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Bad Request")))
                    .andExpect(jsonPath("$.message", is("Current password is incorrect")))
                    .andExpect(jsonPath("$.path", is("/api/users/me/change-password")));
        }
    }

    @Nested
    class DeleteCurrentUser {
        @Test
        @DisplayName("DELETE /api/users/me returns 204 on success")
        void delete_success() throws Exception {
            doNothing().when(userService).deleteUser();

            mockMvc.perform(delete("/api/users/me"))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
        }
    }

    @Nested
    class GetCurrentUser {
        @Test
        @DisplayName("GET /api/users/me returns 200 with UserResponse")
        void get_success() throws Exception {
            UserResponse response = UserResponse.builder()
                    .id(1L)
                    .username("john")
                    .email("john@example.com")
                    .createdAt(LocalDateTime.now().minusDays(20))
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(userService.getUser()).thenReturn(response);

            mockMvc.perform(get("/api/users/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.username", is("john")))
                    .andExpect(jsonPath("$.email", is("john@example.com")));
        }
    }

    @Nested
    class GetCurrentUserStatistics {
        @Test
        @DisplayName("GET /api/users/me/statistics returns 200 with UserStatisticsResponse")
        void statistics_success() throws Exception {
            UserStatisticsResponse stats = UserStatisticsResponse.builder()
                    .totalIncome(new BigDecimal("1000.00"))
                    .totalExpenses(new BigDecimal("400.00"))
                    .netBalance(new BigDecimal("600.00"))
                    .totalTransactions(10L)
                    .totalIncomeTransactions(4L)
                    .totalExpenseTransactions(6L)
                    .averageIncomePerTransaction(new BigDecimal("250.00"))
                    .averageExpensePerTransaction(new BigDecimal("66.67"))
                    .monthlyIncome(new BigDecimal("500.00"))
                    .monthlyExpenses(new BigDecimal("200.00"))
                    .monthlyNetBalance(new BigDecimal("300.00"))
                    .build();

            when(userService.getStatistics()).thenReturn(stats);

            mockMvc.perform(get("/api/users/me/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalIncome", is(1000.00)))
                    .andExpect(jsonPath("$.totalExpenses", is(400.00)))
                    .andExpect(jsonPath("$.netBalance", is(600.00)))
                    .andExpect(jsonPath("$.totalTransactions", is(10)))
                    .andExpect(jsonPath("$.totalIncomeTransactions", is(4)))
                    .andExpect(jsonPath("$.totalExpenseTransactions", is(6)))
                    .andExpect(jsonPath("$.averageIncomePerTransaction", is(250.00)))
                    .andExpect(jsonPath("$.averageExpensePerTransaction", is(66.67)))
                    .andExpect(jsonPath("$.monthlyIncome", is(500.00)))
                    .andExpect(jsonPath("$.monthlyExpenses", is(200.00)))
                    .andExpect(jsonPath("$.monthlyNetBalance", is(300.00)));
        }
    }
}
