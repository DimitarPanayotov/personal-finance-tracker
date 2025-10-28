package com.dimitar.financetracker.integration;

import com.dimitar.financetracker.dto.request.category.CreateCategoryRequest;
import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.request.transaction.CreateTransactionRequest;
import com.dimitar.financetracker.dto.request.transaction.UpdateTransactionRequest;
import com.dimitar.financetracker.dto.response.user.AuthenticationResponse;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.repository.TransactionRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private String jwtToken;
    private Long categoryId;

    @BeforeEach
    void setUp() throws Exception {
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        UserRegistrationRequest registerRequest = new UserRegistrationRequest(
                "transaction_user",
                "transaction@example.com",
                "TransPass123!"
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        AuthenticationResponse authResponse = objectMapper.readValue(responseBody, AuthenticationResponse.class);
        jwtToken = authResponse.getToken();

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest("Food", CategoryType.EXPENSE, "#FF5733");
        MvcResult categoryResult = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String categoryResponse = categoryResult.getResponse().getContentAsString();
        categoryId = objectMapper.readTree(categoryResponse).get("id").asLong();
    }

    @Test
    @DisplayName("Should create a transaction successfully")
    void testCreateTransaction() throws Exception {
        CreateTransactionRequest transactionRequest = new CreateTransactionRequest(
                categoryId,
                BigDecimal.valueOf(50.75),
                "Lunch at restaurant",
                LocalDate.of(2025, 10, 15)
        );

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(50.75))
                .andExpect(jsonPath("$.description").value("Lunch at restaurant"))
                .andExpect(jsonPath("$.categoryId").value(categoryId));

        assertThat(transactionRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should retrieve all user transactions")
    void testGetAllTransactions() throws Exception {
        createTransaction(BigDecimal.valueOf(100), "Grocery shopping");
        createTransaction(BigDecimal.valueOf(3000), "Monthly salary");
        createTransaction(BigDecimal.valueOf(25.50), "Coffee");

        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].amount").exists())
                .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    @DisplayName("Should filter transactions by date range")
    void testGetTransactionsByDateRange() throws Exception {
        createTransactionWithDate(BigDecimal.valueOf(50), LocalDate.of(2025, 10, 1));
        createTransactionWithDate(BigDecimal.valueOf(75), LocalDate.of(2025, 10, 15));
        createTransactionWithDate(BigDecimal.valueOf(100), LocalDate.of(2025, 10, 30));

        mockMvc.perform(get("/api/transactions/date-range")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("startDate", "2025-10-10")
                        .param("endDate", "2025-10-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].amount").value(75));
    }

    @Test
    @DisplayName("Should update a transaction successfully")
    void testUpdateTransaction() throws Exception {
        MvcResult createResult = createTransaction(BigDecimal.valueOf(50), "Original description");
        String createResponse = createResult.getResponse().getContentAsString();
        Long transactionId = objectMapper.readTree(createResponse).get("id").asLong();

        UpdateTransactionRequest updateRequest = UpdateTransactionRequest.builder()
                .categoryId(categoryId)
                .amount(BigDecimal.valueOf(75.50))
                .description("Updated description")
                .transactionDate(LocalDate.of(2025, 10, 20))
                .build();

        mockMvc.perform(patch("/api/transactions/" + transactionId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(75.50))
                .andExpect(jsonPath("$.description").value("Updated description"));

        assertThat(transactionRepository.findById(transactionId))
                .isPresent()
                .get()
                .satisfies(transaction -> {
                    assertThat(transaction.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(75.50));
                    assertThat(transaction.getDescription()).isEqualTo("Updated description");
                });
    }

    @Test
    @DisplayName("Should delete a transaction successfully")
    void testDeleteTransaction() throws Exception {
        MvcResult createResult = createTransaction(BigDecimal.valueOf(100), "To be deleted");
        String createResponse = createResult.getResponse().getContentAsString();
        Long transactionId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/api/transactions/" + transactionId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        assertThat(transactionRepository.findById(transactionId)).isEmpty();
    }

    @Test
    @DisplayName("Should reject transaction with invalid category")
    void testCreateTransactionWithInvalidCategory() throws Exception {
        CreateTransactionRequest invalidRequest = new CreateTransactionRequest(
                999999L,
                BigDecimal.valueOf(50),
                "Invalid transaction",
                LocalDate.now()
        );

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should reject transaction with negative amount")
    void testCreateTransactionWithNegativeAmount() throws Exception {
        CreateTransactionRequest invalidRequest = new CreateTransactionRequest(
                categoryId,
                BigDecimal.valueOf(-50),
                "Invalid amount",
                LocalDate.now()
        );

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    private MvcResult createTransaction(BigDecimal amount, String description) throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                categoryId,
                amount,
                description,
                LocalDate.now()
        );

        return mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private MvcResult createTransactionWithDate(BigDecimal amount, LocalDate date) throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                categoryId,
                amount,
                "Transaction on " + date,
                date
        );

        return mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
    }
}
