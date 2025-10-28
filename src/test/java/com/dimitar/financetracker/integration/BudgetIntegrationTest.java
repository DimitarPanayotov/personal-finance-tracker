package com.dimitar.financetracker.integration;

import com.dimitar.financetracker.dto.request.budget.CreateBudgetRequest;
import com.dimitar.financetracker.dto.request.budget.UpdateBudgetRequest;
import com.dimitar.financetracker.dto.request.category.CreateCategoryRequest;
import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.request.transaction.CreateTransactionRequest;
import com.dimitar.financetracker.dto.response.user.AuthenticationResponse;
import com.dimitar.financetracker.model.BudgetPeriod;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.repository.BudgetRepository;
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
class BudgetIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private String jwtToken;
    private Long categoryId;

    @BeforeEach
    void setUp() throws Exception {
        transactionRepository.deleteAll();
        budgetRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        UserRegistrationRequest registerRequest = new UserRegistrationRequest(
                "budget_user",
                "budget@example.com",
                "BudgetPass123!"
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        AuthenticationResponse authResponse = objectMapper.readValue(responseBody, AuthenticationResponse.class);
        jwtToken = authResponse.getToken();

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest("Entertainment", CategoryType.EXPENSE, "#9C27B0");
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
    @DisplayName("Should create a budget successfully")
    void testCreateBudget() throws Exception {
        CreateBudgetRequest budgetRequest = new CreateBudgetRequest(
                categoryId,
                BigDecimal.valueOf(500),
                BudgetPeriod.MONTHLY,
                LocalDate.of(2025, 10, 1),
                LocalDate.of(2025, 10, 31)
        );

        mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(budgetRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(500))
                .andExpect(jsonPath("$.categoryId").value(categoryId))
                .andExpect(jsonPath("$.isActive").value(true));

        assertThat(budgetRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should track budget usage based on transactions")
    void testBudgetUsageTracking() throws Exception {
        CreateBudgetRequest budgetRequest = new CreateBudgetRequest(
                categoryId,
                BigDecimal.valueOf(500),
                BudgetPeriod.MONTHLY,
                LocalDate.of(2025, 10, 1),
                LocalDate.of(2025, 10, 31)
        );

        MvcResult budgetResult = mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(budgetRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String budgetResponse = budgetResult.getResponse().getContentAsString();
        Long budgetId = objectMapper.readTree(budgetResponse).get("id").asLong();

        createTransaction(BigDecimal.valueOf(150), LocalDate.of(2025, 10, 5));
        createTransaction(BigDecimal.valueOf(100), LocalDate.of(2025, 10, 15));

        mockMvc.perform(get("/api/budgets/" + budgetId + "/usage")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(500))
                .andExpect(jsonPath("$.spent").value(250))
                .andExpect(jsonPath("$.remaining").value(250))
                .andExpect(jsonPath("$.percentUsed").value(50.0));
    }

    @Test
    @DisplayName("Should only count transactions within budget period")
    void testBudgetUsageOnlyCountsRelevantPeriod() throws Exception {
        CreateBudgetRequest budgetRequest = new CreateBudgetRequest(
                categoryId,
                BigDecimal.valueOf(500),
                BudgetPeriod.MONTHLY,
                LocalDate.of(2025, 10, 1),
                LocalDate.of(2025, 10, 31)
        );

        MvcResult budgetResult = mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(budgetRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String budgetResponse = budgetResult.getResponse().getContentAsString();
        Long budgetId = objectMapper.readTree(budgetResponse).get("id").asLong();

        createTransaction(BigDecimal.valueOf(100), LocalDate.of(2025, 9, 25));
        createTransaction(BigDecimal.valueOf(200), LocalDate.of(2025, 10, 15));
        createTransaction(BigDecimal.valueOf(300), LocalDate.of(2025, 11, 5));

        mockMvc.perform(get("/api/budgets/" + budgetId + "/usage")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spent").value(200))
                .andExpect(jsonPath("$.percentUsed").value(40.0));
    }

    @Test
    @DisplayName("Should retrieve all active budgets")
    void testGetActiveBudgets() throws Exception {
        createBudget(categoryId, BigDecimal.valueOf(500), LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 31));
        createBudget(categoryId, BigDecimal.valueOf(300), LocalDate.of(2025, 11, 1), LocalDate.of(2025, 11, 30));

        mockMvc.perform(get("/api/budgets/active")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Should deactivate a budget")
    void testDeactivateBudget() throws Exception {
        MvcResult budgetResult = createBudget(
                categoryId,
                BigDecimal.valueOf(500),
                LocalDate.of(2025, 10, 1),
                LocalDate.of(2025, 10, 31)
        );
        String budgetResponse = budgetResult.getResponse().getContentAsString();
        Long budgetId = objectMapper.readTree(budgetResponse).get("id").asLong();

        mockMvc.perform(post("/api/budgets/" + budgetId + "/deactivate")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));

        mockMvc.perform(get("/api/budgets/active")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should update a budget successfully")
    void testUpdateBudget() throws Exception {
        MvcResult budgetResult = createBudget(
                categoryId,
                BigDecimal.valueOf(500),
                LocalDate.of(2025, 10, 1),
                LocalDate.of(2025, 10, 31)
        );
        String budgetResponse = budgetResult.getResponse().getContentAsString();
        Long budgetId = objectMapper.readTree(budgetResponse).get("id").asLong();

        UpdateBudgetRequest updateRequest = UpdateBudgetRequest.builder()
                .amount(BigDecimal.valueOf(750))
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.of(2025, 10, 1))
                .endDate(LocalDate.of(2025, 10, 31))
                .build();

        mockMvc.perform(patch("/api/budgets/" + budgetId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(750));
    }

    @Test
    @DisplayName("Should delete a budget successfully")
    void testDeleteBudget() throws Exception {
        MvcResult budgetResult = createBudget(
                categoryId,
                BigDecimal.valueOf(500),
                LocalDate.of(2025, 10, 1),
                LocalDate.of(2025, 10, 31)
        );
        String budgetResponse = budgetResult.getResponse().getContentAsString();
        Long budgetId = objectMapper.readTree(budgetResponse).get("id").asLong();

        mockMvc.perform(delete("/api/budgets/" + budgetId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        assertThat(budgetRepository.findById(budgetId)).isEmpty();
    }

    private void createTransaction(BigDecimal amount, LocalDate date) throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                categoryId,
                amount,
                "Transaction on " + date,
                date
        );

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private MvcResult createBudget(Long categoryId, BigDecimal amount, LocalDate startDate, LocalDate endDate) throws Exception {
        CreateBudgetRequest request = new CreateBudgetRequest(categoryId, amount, BudgetPeriod.MONTHLY, startDate, endDate);

        return mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
    }
}
