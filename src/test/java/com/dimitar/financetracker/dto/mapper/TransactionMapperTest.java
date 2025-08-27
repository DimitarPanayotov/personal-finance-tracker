package com.dimitar.financetracker.dto.mapper;

import com.dimitar.financetracker.dto.request.transaction.CreateTransactionRequest;
import com.dimitar.financetracker.dto.request.transaction.UpdateTransactionRequest;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.dto.response.transaction.TransactionSummaryResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.model.CategoryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {

    private TransactionMapper transactionMapper;
    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        transactionMapper = new TransactionMapper();
        
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        testCategory = Category.builder()
                .id(1L)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .user(testUser)
                .build();
    }

    @Test
    void toEntity_validRequest_shouldMapCorrectly() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.50"))
                .description("Test transaction")
                .transactionDate(LocalDate.of(2023, 1, 1))
                .build();

        Transaction result = transactionMapper.toEntity(request, testUser, testCategory);

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getCategory()).isEqualTo(testCategory);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("100.50"));
        assertThat(result.getDescription()).isEqualTo("Test transaction");
        assertThat(result.getTransactionDate()).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(result.getId()).isNull();
    }

    @Test
    void toEntity_nullRequest_shouldReturnNull() {
        Transaction result = transactionMapper.toEntity(null, testUser, testCategory);

        assertThat(result).isNull();
    }

    @Test
    void toEntity_requestWithNullDescription_shouldMapCorrectly() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.50"))
                .description(null)
                .transactionDate(LocalDate.of(2023, 1, 1))
                .build();

        Transaction result = transactionMapper.toEntity(request, testUser, testCategory);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isNull();
    }

    @Test
    void toResponse_validTransaction_shouldMapCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        Transaction transaction = Transaction.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("150.75"))
                .description("Test transaction")
                .transactionDate(LocalDate.of(2023, 1, 1))
                .createdAt(now)
                .updatedAt(now)
                .build();

        TransactionResponse result = transactionMapper.toResponse(transaction);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getCategoryId()).isEqualTo(1L);
        assertThat(result.getCategoryName()).isEqualTo("Food");
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("150.75"));
        assertThat(result.getDescription()).isEqualTo("Test transaction");
        assertThat(result.getTransactionDate()).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(result.getCreatedAt()).isEqualTo(now);
        assertThat(result.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toResponse_nullTransaction_shouldReturnNull() {
        TransactionResponse result = transactionMapper.toResponse(null);

        assertThat(result).isNull();
    }

    @Test
    void toSummaryResponse_validTransaction_shouldMapCorrectly() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("150.75"))
                .description("Test transaction")
                .transactionDate(LocalDate.of(2023, 1, 1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TransactionSummaryResponse result = transactionMapper.toSummaryResponse(transaction);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getCategoryId()).isEqualTo(1L);
        assertThat(result.getCategoryName()).isEqualTo("Food");
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("150.75"));
        assertThat(result.getDescription()).isEqualTo("Test transaction");
        assertThat(result.getTransactionDate()).isEqualTo(LocalDate.of(2023, 1, 1));
    }

    @Test
    void toSummaryResponse_nullTransaction_shouldReturnNull() {
        TransactionSummaryResponse result = transactionMapper.toSummaryResponse(null);

        assertThat(result).isNull();
    }

    @Test
    void updateEntity_validRequest_shouldUpdateAllFields() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("100.00"))
                .description("Original description")
                .transactionDate(LocalDate.of(2023, 1, 1))
                .build();

        Category newCategory = Category.builder()
                .id(2L)
                .name("Entertainment")
                .user(testUser)
                .build();

        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .categoryId(2L)
                .amount(new BigDecimal("200.00"))
                .description("Updated description")
                .transactionDate(LocalDate.of(2023, 2, 1))
                .build();

        transactionMapper.updateEntity(transaction, request, newCategory);

        assertThat(transaction.getCategory()).isEqualTo(newCategory);
        assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("200.00"));
        assertThat(transaction.getDescription()).isEqualTo("Updated description");
        assertThat(transaction.getTransactionDate()).isEqualTo(LocalDate.of(2023, 2, 1));
    }

    @Test
    void updateEntity_partialUpdate_shouldUpdateOnlyProvidedFields() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("100.00"))
                .description("Original description")
                .transactionDate(LocalDate.of(2023, 1, 1))
                .build();

        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .amount(new BigDecimal("200.00"))
                .build();

        transactionMapper.updateEntity(transaction, request, null);

        assertThat(transaction.getCategory()).isEqualTo(testCategory); // Unchanged
        assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("200.00")); // Updated
        assertThat(transaction.getDescription()).isEqualTo("Original description"); // Unchanged
        assertThat(transaction.getTransactionDate()).isEqualTo(LocalDate.of(2023, 1, 1)); // Unchanged
    }

    @Test
    void updateEntity_descriptionWithWhitespace_shouldTrim() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("100.00"))
                .description("Original description")
                .transactionDate(LocalDate.of(2023, 1, 1))
                .build();

        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .description("  Updated description  ")
                .build();

        transactionMapper.updateEntity(transaction, request, null);

        assertThat(transaction.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void updateEntity_nullTransaction_shouldNotThrowException() {
        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .amount(new BigDecimal("200.00"))
                .build();

        // Should not throw any exception
        transactionMapper.updateEntity(null, request, testCategory);
    }

    @Test
    void updateEntity_nullRequest_shouldNotThrowException() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("100.00"))
                .description("Original description")
                .transactionDate(LocalDate.of(2023, 1, 1))
                .build();

        // Should not throw any exception
        transactionMapper.updateEntity(transaction, null, testCategory);

        // Transaction should remain unchanged
        assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(transaction.getDescription()).isEqualTo("Original description");
    }

    @Test
    void updateEntity_nullCategory_shouldNotUpdateCategory() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("100.00"))
                .description("Original description")
                .transactionDate(LocalDate.of(2023, 1, 1))
                .build();

        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .categoryId(2L)
                .amount(new BigDecimal("200.00"))
                .build();

        transactionMapper.updateEntity(transaction, request, null);

        assertThat(transaction.getCategory()).isEqualTo(testCategory); // Should remain unchanged
        assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("200.00")); // Should be updated
    }
}
