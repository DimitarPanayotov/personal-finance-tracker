package com.dimitar.financetracker.controller;

import com.dimitar.financetracker.dto.request.budget.CreateBudgetRequest;
import com.dimitar.financetracker.dto.request.budget.UpdateBudgetRequest;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.dto.response.budget.BudgetUsageResponse;
import com.dimitar.financetracker.exception.GlobalExceptionHandler;
import com.dimitar.financetracker.exception.budget.BudgetDoesNotExistException;
import com.dimitar.financetracker.model.BudgetPeriod;
import com.dimitar.financetracker.service.BudgetService;
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
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    BudgetService budgetService;

    @BeforeEach
    void setUp() {
        BudgetController controller = new BudgetController(budgetService);
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
    class CreateBudget {
        @Test
        @DisplayName("POST /api/budgets returns 201 with BudgetResponse on success")
        void create_success() throws Exception {
            BudgetResponse resp = BudgetResponse.builder()
                    .id(1L)
                    .userId(100L)
                    .categoryId(10L)
                    .categoryName("Food")
                    .amount(new BigDecimal("200.00"))
                    .period(BudgetPeriod.MONTHLY)
                    .startDate(LocalDate.of(2025, 1, 1))
                    .endDate(LocalDate.of(2025, 1, 31))
                    .isActive(true)
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(budgetService.createBudget(any(CreateBudgetRequest.class))).thenReturn(resp);

            CreateBudgetRequest req = CreateBudgetRequest.builder()
                    .categoryId(10L)
                    .amount(new BigDecimal("200.00"))
                    .period(BudgetPeriod.MONTHLY)
                    .startDate(LocalDate.of(2025, 1, 1))
                    .endDate(LocalDate.of(2025, 1, 31))
                    .build();

            mockMvc.perform(post("/api/budgets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.categoryId", is(10)))
                    .andExpect(jsonPath("$.amount", is(200.00)))
                    .andExpect(jsonPath("$.period", is("MONTHLY")))
                    .andExpect(jsonPath("$.startDate", is("2025-01-01")));
        }

        @Test
        @DisplayName("POST /api/budgets returns 400 with validation errors when payload invalid")
        void create_validationErrors() throws Exception {
            CreateBudgetRequest invalid = CreateBudgetRequest.builder()
                    .categoryId(null)
                    .amount(null)
                    .period(null)
                    .startDate(null)
                    .build();

            mockMvc.perform(post("/api/budgets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")))
                    .andExpect(jsonPath("$.message", is("Validation failed")))
                    .andExpect(jsonPath("$.path", is("/api/budgets")))
                    .andExpect(jsonPath("$.errors.categoryId", is("Category is required")))
                    .andExpect(jsonPath("$.errors.amount", is("Amount is required")))
                    .andExpect(jsonPath("$.errors.period", is("Budget period is required")))
                    .andExpect(jsonPath("$.errors.startDate", is("Start date is required")));
        }

        @Test
        @DisplayName("POST /api/budgets returns 400 when amount below minimum")
        void create_amountTooSmall() throws Exception {
            CreateBudgetRequest invalid = CreateBudgetRequest.builder()
                    .categoryId(1L)
                    .amount(new BigDecimal("0.001"))
                    .period(BudgetPeriod.WEEKLY)
                    .startDate(LocalDate.of(2025, 1, 1))
                    .build();

            mockMvc.perform(post("/api/budgets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.amount", is("Budget amount must be at least 0.01")));
        }
    }

    @Nested
    class ActivateDeactivateBudget {
        @Test
        @DisplayName("POST /api/budgets/{id}/deactivate returns 200 with BudgetResponse")
        void deactivate_success() throws Exception {
            BudgetResponse resp = BudgetResponse.builder().id(5L).isActive(false).build();
            when(budgetService.deactivateBudget(5L)).thenReturn(resp);

            mockMvc.perform(post("/api/budgets/{id}/deactivate", 5))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(5)))
                    .andExpect(jsonPath("$.isActive", is(false)));
        }

        @Test
        @DisplayName("POST /api/budgets/{id}/deactivate returns 404 when not found")
        void deactivate_notFound() throws Exception {
            when(budgetService.deactivateBudget(999L))
                    .thenThrow(new BudgetDoesNotExistException("Budget not found"));

            mockMvc.perform(post("/api/budgets/{id}/deactivate", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("Budget not found")))
                    .andExpect(jsonPath("$.path", is("/api/budgets/999/deactivate")));
        }

        @Test
        @DisplayName("POST /api/budgets/{id}/activate returns 200 with BudgetResponse")
        void activate_success() throws Exception {
            BudgetResponse resp = BudgetResponse.builder().id(6L).isActive(true).build();
            when(budgetService.activateBudget(6L)).thenReturn(resp);

            mockMvc.perform(post("/api/budgets/{id}/activate", 6))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(6)))
                    .andExpect(jsonPath("$.isActive", is(true)));
        }

        @Test
        @DisplayName("POST /api/budgets/{id}/activate returns 404 when not found")
        void activate_notFound() throws Exception {
            when(budgetService.activateBudget(999L))
                    .thenThrow(new BudgetDoesNotExistException("Budget not found"));

            mockMvc.perform(post("/api/budgets/{id}/activate", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("Budget not found")))
                    .andExpect(jsonPath("$.path", is("/api/budgets/999/activate")));
        }
    }

    @Nested
    class UpdateBudget {
        @Test
        @DisplayName("PATCH /api/budgets/{id} returns 200 with BudgetResponse on success")
        void update_success() throws Exception {
            BudgetResponse resp = BudgetResponse.builder()
                    .id(2L)
                    .categoryId(20L)
                    .amount(new BigDecimal("300.00"))
                    .period(BudgetPeriod.YEARLY)
                    .startDate(LocalDate.of(2025, 1, 1))
                    .endDate(LocalDate.of(2025, 12, 31))
                    .isActive(true)
                    .build();
            when(budgetService.updateBudget(any(UpdateBudgetRequest.class))).thenReturn(resp);

            UpdateBudgetRequest req = UpdateBudgetRequest.builder()
                    .categoryId(20L)
                    .amount(new BigDecimal("300.00"))
                    .period(BudgetPeriod.YEARLY)
                    .startDate(LocalDate.of(2025, 1, 1))
                    .endDate(LocalDate.of(2025, 12, 31))
                    .build();

            mockMvc.perform(patch("/api/budgets/{id}", 2)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(2)))
                    .andExpect(jsonPath("$.categoryId", is(20)))
                    .andExpect(jsonPath("$.amount", is(300.00)))
                    .andExpect(jsonPath("$.period", is("YEARLY")));
        }

        @Test
        @DisplayName("PATCH /api/budgets/{id} returns 400 when amount below minimum")
        void update_validationErrors() throws Exception {
            UpdateBudgetRequest invalid = UpdateBudgetRequest.builder()
                    .amount(new BigDecimal("0.0"))
                    .build();

            mockMvc.perform(patch("/api/budgets/{id}", 2)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.amount", is("Budget amount must be at least 0.01")));
        }

        @Test
        @DisplayName("PATCH /api/budgets/{id} returns 404 when budget not found")
        void update_notFound() throws Exception {
            when(budgetService.updateBudget(any(UpdateBudgetRequest.class)))
                    .thenThrow(new BudgetDoesNotExistException("Budget not found"));

            UpdateBudgetRequest req = UpdateBudgetRequest.builder()
                    .amount(new BigDecimal("500.00"))
                    .build();

            mockMvc.perform(patch("/api/budgets/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("Budget not found")))
                    .andExpect(jsonPath("$.path", is("/api/budgets/999")));
        }
    }

    @Nested
    class DeleteBudget {
        @Test
        @DisplayName("DELETE /api/budgets/{id} returns 204 on success")
        void delete_success() throws Exception {
            doNothing().when(budgetService).deleteBudget(3L);

            mockMvc.perform(delete("/api/budgets/{id}", 3))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("DELETE /api/budgets/{id} returns 404 when not found")
        void delete_notFound() throws Exception {
            doThrow(new BudgetDoesNotExistException("Budget not found"))
                    .when(budgetService).deleteBudget(999L);

            mockMvc.perform(delete("/api/budgets/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("Budget not found")))
                    .andExpect(jsonPath("$.path", is("/api/budgets/999")));
        }
    }

    @Nested
    class GetBudgets {
        @Test
        @DisplayName("GET /api/budgets returns 200 with list")
        void getAll_success() throws Exception {
            BudgetResponse b1 = BudgetResponse.builder().id(1L).categoryId(10L).amount(new BigDecimal("100.00")).period(BudgetPeriod.MONTHLY).build();
            BudgetResponse b2 = BudgetResponse.builder().id(2L).categoryId(11L).amount(new BigDecimal("200.00")).period(BudgetPeriod.WEEKLY).build();
            when(budgetService.getAllBudgets()).thenReturn(List.of(b1, b2));

            mockMvc.perform(get("/api/budgets"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[1].period", is("WEEKLY")));
        }

        @Test
        @DisplayName("GET /api/budgets/active returns 200 with list")
        void getActive_success() throws Exception {
            BudgetResponse b = BudgetResponse.builder().id(1L).isActive(true).build();
            when(budgetService.getActiveBudgets()).thenReturn(List.of(b));

            mockMvc.perform(get("/api/budgets/active"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].isActive", is(true)));
        }

        @Test
        @DisplayName("GET /api/budgets/{id} returns 200 with BudgetResponse")
        void getById_success() throws Exception {
            BudgetResponse b = BudgetResponse.builder().id(7L).categoryId(10L).amount(new BigDecimal("150.00")).build();
            when(budgetService.getBudgetById(7L)).thenReturn(b);

            mockMvc.perform(get("/api/budgets/{id}", 7))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(7)))
                    .andExpect(jsonPath("$.amount", is(150.00)));
        }

        @Test
        @DisplayName("GET /api/budgets/{id} returns 404 when not found")
        void getById_notFound() throws Exception {
            when(budgetService.getBudgetById(999L))
                    .thenThrow(new BudgetDoesNotExistException("Budget not found"));

            mockMvc.perform(get("/api/budgets/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("Budget not found")))
                    .andExpect(jsonPath("$.path", is("/api/budgets/999")));
        }

        @Test
        @DisplayName("GET /api/budgets/category/{categoryId} returns 200 with list")
        void getByCategory_success() throws Exception {
            BudgetResponse b = BudgetResponse.builder().id(1L).categoryId(15L).build();
            when(budgetService.getBudgetsByCategory(15L)).thenReturn(List.of(b));

            mockMvc.perform(get("/api/budgets/category/{categoryId}", 15))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].categoryId", is(15)));
        }

        @Test
        @DisplayName("GET /api/budgets/{id}/usage returns 200 with BudgetUsageResponse")
        void getUsage_success() throws Exception {
            BudgetUsageResponse u = BudgetUsageResponse.builder()
                    .id(1L)
                    .categoryId(10L)
                    .amount(new BigDecimal("200.00"))
                    .spent(new BigDecimal("50.00"))
                    .remaining(new BigDecimal("150.00"))
                    .percentUsed(new BigDecimal("25.00"))
                    .period(BudgetPeriod.MONTHLY)
                    .build();
            when(budgetService.getBudgetUsage(1L)).thenReturn(u);

            mockMvc.perform(get("/api/budgets/{id}/usage", 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.spent", is(50.00)))
                    .andExpect(jsonPath("$.percentUsed", is(25.00)));
        }

        @Test
        @DisplayName("GET /api/budgets/{id}/usage returns 404 when not found")
        void getUsage_notFound() throws Exception {
            when(budgetService.getBudgetUsage(999L))
                    .thenThrow(new BudgetDoesNotExistException("Budget not found"));

            mockMvc.perform(get("/api/budgets/{id}/usage", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("Budget not found")))
                    .andExpect(jsonPath("$.path", is("/api/budgets/999/usage")));
        }

        @Test
        @DisplayName("GET /api/budgets/usage returns 200 with list of BudgetUsageResponse")
        void getAllUsage_success() throws Exception {
            BudgetUsageResponse u = BudgetUsageResponse.builder().id(1L).percentUsed(new BigDecimal("50.00")).build();
            when(budgetService.getAllBudgetsUsage()).thenReturn(List.of(u));

            mockMvc.perform(get("/api/budgets/usage"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].percentUsed", is(50.00)));
        }
    }
}
