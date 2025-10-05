package com.dimitar.financetracker.controller;

import com.dimitar.financetracker.dto.request.transaction.CreateTransactionRequest;
import com.dimitar.financetracker.dto.request.transaction.UpdateTransactionRequest;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.exception.GlobalExceptionHandler;
import com.dimitar.financetracker.exception.transaction.TransactionDoesNotExistException;
import com.dimitar.financetracker.service.TransactionService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    TransactionService transactionService;

    @BeforeEach
    void setUp() {
        TransactionController controller = new TransactionController(transactionService);
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
    class CreateTransactionTests {
        @Test
        @DisplayName("POST /api/transactions returns 201 with TransactionResponse on success")
        void create_success() throws Exception {
            TransactionResponse response = TransactionResponse.builder()
                    .id(1L)
                    .userId(100L)
                    .categoryId(10L)
                    .categoryName("Food")
                    .amount(new BigDecimal("25.50"))
                    .description("Lunch")
                    .transactionDate(LocalDate.of(2025, 1, 1))
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(transactionService.createTransaction(any(CreateTransactionRequest.class)))
                    .thenReturn(response);

            CreateTransactionRequest request = CreateTransactionRequest.builder()
                    .categoryId(10L)
                    .amount(new BigDecimal("25.50"))
                    .description("Lunch")
                    .transactionDate(LocalDate.of(2025, 1, 1))
                    .build();

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.categoryId", is(10)))
                    .andExpect(jsonPath("$.amount", is(25.50)))
                    .andExpect(jsonPath("$.description", is("Lunch")))
                    .andExpect(jsonPath("$.transactionDate", is("2025-01-01")));
        }

        @Test
        @DisplayName("POST /api/transactions returns 400 with validation errors when payload invalid")
        void create_validationErrors() throws Exception {
            String longDesc = "x".repeat(260);
            CreateTransactionRequest invalid = CreateTransactionRequest.builder()
                    .categoryId(null)
                    .amount(null)
                    .description(longDesc)
                    .transactionDate(null)
                    .build();

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")))
                    .andExpect(jsonPath("$.message", is("Validation failed for user registration")))
                    .andExpect(jsonPath("$.path", is("/api/transactions")))
                    .andExpect(jsonPath("$.errors.categoryId", is("Category is required")))
                    .andExpect(jsonPath("$.errors.amount", is("Amount is required")))
                    .andExpect(jsonPath("$.errors.description", is("Description must be less than 255 characters")))
                    .andExpect(jsonPath("$.errors.transactionDate", is("Transaction date is required")));
        }

        @Test
        @DisplayName("POST /api/transactions returns 400 when amount below minimum")
        void create_amountTooSmall() throws Exception {
            CreateTransactionRequest invalid = CreateTransactionRequest.builder()
                    .categoryId(5L)
                    .amount(new BigDecimal("0.001"))
                    .transactionDate(LocalDate.of(2025, 1, 1))
                    .build();

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.amount", is("Amount must be at least 0.01")));
        }
    }

    @Nested
    class DuplicateTransactionTests {
        @Test
        @DisplayName("POST /api/transactions/{id}/duplicate returns 201 on success")
        void duplicate_success() throws Exception {
            TransactionResponse resp = TransactionResponse.builder()
                    .id(2L)
                    .userId(100L)
                    .categoryId(10L)
                    .amount(new BigDecimal("25.50"))
                    .description("Lunch (copy)")
                    .transactionDate(LocalDate.of(2025, 1, 1))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(transactionService.duplicateTransaction(1L)).thenReturn(resp);

            mockMvc.perform(post("/api/transactions/{id}/duplicate", 1))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(2)))
                    .andExpect(jsonPath("$.description", containsString("copy")));
        }

        @Test
        @DisplayName("POST /api/transactions/{id}/duplicate returns 404 when not found")
        void duplicate_notFound() throws Exception {
            when(transactionService.duplicateTransaction(999L))
                    .thenThrow(new TransactionDoesNotExistException("Transaction not found"));

            mockMvc.perform(post("/api/transactions/{id}/duplicate", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("Transaction not found")))
                    .andExpect(jsonPath("$.path", is("/api/transactions/999/duplicate")));
        }
    }

    @Nested
    class UpdateTransactionTests {
        @Test
        @DisplayName("PATCH /api/transactions/{id} returns 200 with TransactionResponse on success")
        void update_success() throws Exception {
            TransactionResponse resp = TransactionResponse.builder()
                    .id(1L)
                    .categoryId(20L)
                    .amount(new BigDecimal("12.00"))
                    .description("Dinner")
                    .transactionDate(LocalDate.of(2025, 2, 2))
                    .createdAt(LocalDateTime.now().minusDays(3))
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(transactionService.updateTransaction(any(UpdateTransactionRequest.class)))
                    .thenReturn(resp);

            UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                    .categoryId(20L)
                    .amount(new BigDecimal("12.00"))
                    .description("Dinner")
                    .transactionDate(LocalDate.of(2025, 2, 2))
                    .build();

            mockMvc.perform(patch("/api/transactions/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.categoryId", is(20)))
                    .andExpect(jsonPath("$.amount", is(12.00)))
                    .andExpect(jsonPath("$.description", is("Dinner")))
                    .andExpect(jsonPath("$.transactionDate", is("2025-02-02")));
        }

        @Test
        @DisplayName("PATCH /api/transactions/{id} returns 400 when amount too small or description too long")
        void update_validationErrors() throws Exception {
            String longDesc = "x".repeat(300);
            UpdateTransactionRequest invalid = UpdateTransactionRequest.builder()
                    .amount(new BigDecimal("0"))
                    .description(longDesc)
                    .build();

            mockMvc.perform(patch("/api/transactions/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.amount", is("Amount must be at least 0.01")))
                    .andExpect(jsonPath("$.errors.description", is("Description must be less than 255 characters")));
        }

        @Test
        @DisplayName("PATCH /api/transactions/{id} returns 404 when not found")
        void update_notFound() throws Exception {
            when(transactionService.updateTransaction(any(UpdateTransactionRequest.class)))
                    .thenThrow(new TransactionDoesNotExistException("Transaction not found"));

            UpdateTransactionRequest req = UpdateTransactionRequest.builder()
                    .amount(new BigDecimal("15.00"))
                    .build();

            mockMvc.perform(patch("/api/transactions/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("Transaction not found")))
                    .andExpect(jsonPath("$.path", is("/api/transactions/999")));
        }
    }

    @Nested
    class DeleteTransactionTests {
        @Test
        @DisplayName("DELETE /api/transactions/{id} returns 204 on success")
        void delete_success() throws Exception {
            doNothing().when(transactionService).deleteTransaction(1L);

            mockMvc.perform(delete("/api/transactions/{id}", 1))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("DELETE /api/transactions/{id} returns 404 when not found")
        void delete_notFound() throws Exception {
            doThrow(new TransactionDoesNotExistException("Transaction not found"))
                    .when(transactionService).deleteTransaction(999L);

            mockMvc.perform(delete("/api/transactions/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("Transaction not found")))
                    .andExpect(jsonPath("$.path", is("/api/transactions/999")));
        }
    }

    @Nested
    class GetTransactionsTests {
        @Test
        @DisplayName("GET /api/transactions returns 200 with list")
        void getAll_success() throws Exception {
            TransactionResponse t1 = TransactionResponse.builder().id(1L).categoryId(10L).amount(new BigDecimal("5.00")).transactionDate(LocalDate.of(2025,1,1)).build();
            TransactionResponse t2 = TransactionResponse.builder().id(2L).categoryId(11L).amount(new BigDecimal("6.00")).transactionDate(LocalDate.of(2025,1,2)).build();
            when(transactionService.getAllTransactions()).thenReturn(List.of(t1, t2));

            mockMvc.perform(get("/api/transactions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[1].id", is(2)));
        }

        @Test
        @DisplayName("GET /api/transactions/{id} returns 200 with TransactionResponse")
        void getById_success() throws Exception {
            TransactionResponse t = TransactionResponse.builder().id(5L).categoryId(10L).amount(new BigDecimal("7.50")).transactionDate(LocalDate.of(2025,1,3)).build();
            when(transactionService.getTransactionById(5L)).thenReturn(t);

            mockMvc.perform(get("/api/transactions/{id}", 5))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(5)))
                    .andExpect(jsonPath("$.categoryId", is(10)))
                    .andExpect(jsonPath("$.amount", is(7.50)));
        }

        @Test
        @DisplayName("GET /api/transactions/{id} returns 404 when not found")
        void getById_notFound() throws Exception {
            when(transactionService.getTransactionById(999L))
                    .thenThrow(new TransactionDoesNotExistException("Transaction not found"));

            mockMvc.perform(get("/api/transactions/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("Transaction not found")))
                    .andExpect(jsonPath("$.path", is("/api/transactions/999")));
        }

        @Test
        @DisplayName("GET /api/transactions/date-range returns 200 with list")
        void getByDateRange_success() throws Exception {
            TransactionResponse t = TransactionResponse.builder().id(1L).transactionDate(LocalDate.of(2025,1,1)).amount(new BigDecimal("5.00")).build();
            when(transactionService.getTransactionsInDateRange(LocalDate.of(2025,1,1), LocalDate.of(2025,1,31)))
                    .thenReturn(List.of(t));

            mockMvc.perform(get("/api/transactions/date-range")
                            .param("startDate", "2025-01-01")
                            .param("endDate", "2025-01-31"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(1)));
        }

        @Test
        @DisplayName("GET /api/transactions/category/{categoryId} returns 200 with list")
        void getByCategory_success() throws Exception {
            TransactionResponse t = TransactionResponse.builder().id(1L).categoryId(10L).amount(new BigDecimal("20.00")).build();
            when(transactionService.getTransactionsByCategory(10L)).thenReturn(List.of(t));

            mockMvc.perform(get("/api/transactions/category/{categoryId}", 10))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].categoryId", is(10)));
        }

        @Test
        @DisplayName("GET /api/transactions/amount-range returns 200 with list")
        void getByAmountRange_success() throws Exception {
            TransactionResponse t = TransactionResponse.builder().id(1L).amount(new BigDecimal("50.00")).build();
            when(transactionService.getTransactionsByAmountRange(new BigDecimal("10.00"), new BigDecimal("100.00")))
                    .thenReturn(List.of(t));

            mockMvc.perform(get("/api/transactions/amount-range")
                            .param("minAmount", "10.00")
                            .param("maxAmount", "100.00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].amount", is(50.00)));
        }

        @Test
        @DisplayName("GET /api/transactions/search returns 200 with list")
        void search_success() throws Exception {
            TransactionResponse t = TransactionResponse.builder().id(1L).description("Coffee").build();
            when(transactionService.searchTransactionsByDescription("cof"))
                    .thenReturn(List.of(t));

            mockMvc.perform(get("/api/transactions/search").param("q", "cof"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].description", containsString("Coffee")));
        }

        @Test
        @DisplayName("GET /api/transactions/recent returns 200 with list (with and without limit)")
        void recent_success() throws Exception {
            TransactionResponse t = TransactionResponse.builder().id(1L).build();
            when(transactionService.getRecentTransactions(5)).thenReturn(List.of(t));
            when(transactionService.getRecentTransactions(null)).thenReturn(List.of(t));

            mockMvc.perform(get("/api/transactions/recent").param("limit", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            mockMvc.perform(get("/api/transactions/recent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }
}
