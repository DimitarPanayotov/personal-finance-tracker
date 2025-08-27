package com.dimitar.financetracker.dto.mapper;

import com.dimitar.financetracker.dto.request.budget.CreateBudgetRequest;
import com.dimitar.financetracker.dto.request.budget.UpdateBudgetRequest;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.dto.response.budget.BudgetSummaryResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.model.BudgetPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetMapperTest {

    private BudgetMapper budgetMapper;
    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        budgetMapper = new BudgetMapper();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        testCategory = Category.builder()
                .id(1L)
                .name("Food & Dining")
                .user(testUser)
                .build();
    }

    @Test
    @DisplayName("Should map CreateBudgetRequest to Budget entity")
    void shouldMapCreateBudgetRequestToBudgetEntity() {
        // Given
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("500.00"))
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .build();

        // When
        Budget budget = budgetMapper.toEntity(request, testUser, testCategory);

        // Then
        assertThat(budget).isNotNull();
        assertThat(budget.getUser()).isEqualTo(testUser);
        assertThat(budget.getCategory()).isEqualTo(testCategory);
        assertThat(budget.getAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(budget.getPeriod()).isEqualTo(BudgetPeriod.MONTHLY);
        assertThat(budget.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(budget.getEndDate()).isEqualTo(LocalDate.of(2024, 1, 31));
        assertThat(budget.getId()).isNull(); // Not set during mapping
        assertThat(budget.getIsActive()).isNull(); // Will be set by entity default
        assertThat(budget.getCreatedAt()).isNull(); // Set by @PrePersist
        assertThat(budget.getUpdatedAt()).isNull(); // Set by @PrePersist
    }

    @Test
    @DisplayName("Should map CreateBudgetRequest without endDate to Budget entity")
    void shouldMapCreateBudgetRequestWithoutEndDateToBudgetEntity() {
        // Given
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("200.50"))
                .period(BudgetPeriod.WEEKLY)
                .startDate(LocalDate.of(2024, 2, 1))
                .build();

        // When
        Budget budget = budgetMapper.toEntity(request, testUser, testCategory);

        // Then
        assertThat(budget).isNotNull();
        assertThat(budget.getUser()).isEqualTo(testUser);
        assertThat(budget.getCategory()).isEqualTo(testCategory);
        assertThat(budget.getAmount()).isEqualTo(new BigDecimal("200.50"));
        assertThat(budget.getPeriod()).isEqualTo(BudgetPeriod.WEEKLY);
        assertThat(budget.getStartDate()).isEqualTo(LocalDate.of(2024, 2, 1));
        assertThat(budget.getEndDate()).isNull(); // Will be calculated by entity
    }

    @Test
    @DisplayName("Should return null when CreateBudgetRequest is null")
    void shouldReturnNullWhenCreateBudgetRequestIsNull() {
        // When
        Budget budget = budgetMapper.toEntity(null, testUser, testCategory);

        // Then
        assertThat(budget).isNull();
    }

    @Test
    @DisplayName("Should map Budget entity to BudgetResponse")
    void shouldMapBudgetEntityToBudgetResponse() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Budget budget = Budget.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("750.00"))
                .startDate(LocalDate.of(2024, 3, 1))
                .endDate(LocalDate.of(2024, 5, 31))
                .period(BudgetPeriod.QUARTERLY)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        BudgetResponse response = budgetMapper.toResponse(budget);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getCategoryId()).isEqualTo(1L);
        assertThat(response.getCategoryName()).isEqualTo("Food & Dining");
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("750.00"));
        assertThat(response.getStartDate()).isEqualTo(LocalDate.of(2024, 3, 1));
        assertThat(response.getEndDate()).isEqualTo(LocalDate.of(2024, 5, 31));
        assertThat(response.getPeriod()).isEqualTo(BudgetPeriod.QUARTERLY);
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should return null when Budget entity is null for toResponse")
    void shouldReturnNullWhenBudgetEntityIsNullForToResponse() {
        // When
        BudgetResponse response = budgetMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("Should map Budget entity to BudgetSummaryResponse")
    void shouldMapBudgetEntityToBudgetSummaryResponse() {
        // Given
        Budget budget = Budget.builder()
                .id(2L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("1000.00"))
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .period(BudgetPeriod.YEARLY)
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        BudgetSummaryResponse response = budgetMapper.toSummaryResponse(budget);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getCategoryId()).isEqualTo(1L);
        assertThat(response.getCategoryName()).isEqualTo("Food & Dining");
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(response.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(response.getEndDate()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(response.getPeriod()).isEqualTo(BudgetPeriod.YEARLY);
        assertThat(response.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Should return null when Budget entity is null for toSummaryResponse")
    void shouldReturnNullWhenBudgetEntityIsNullForToSummaryResponse() {
        // When
        BudgetSummaryResponse response = budgetMapper.toSummaryResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("Should update Budget entity with all fields from UpdateBudgetRequest")
    void shouldUpdateBudgetEntityWithAllFieldsFromUpdateBudgetRequest() {
        // Given
        Budget budget = Budget.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("100.00"))
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .period(BudgetPeriod.MONTHLY)
                .isActive(true)
                .build();

        Category newCategory = Category.builder()
                .id(2L)
                .name("Transportation")
                .user(testUser)
                .build();

        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .categoryId(2L)
                .amount(new BigDecimal("300.00"))
                .period(BudgetPeriod.WEEKLY)
                .startDate(LocalDate.of(2024, 2, 1))
                .endDate(LocalDate.of(2024, 2, 7))
                .build();

        // When
        budgetMapper.updateEntity(budget, request, newCategory);

        // Then
        assertThat(budget.getCategory()).isEqualTo(newCategory);
        assertThat(budget.getAmount()).isEqualTo(new BigDecimal("300.00"));
        assertThat(budget.getPeriod()).isEqualTo(BudgetPeriod.WEEKLY);
        assertThat(budget.getStartDate()).isEqualTo(LocalDate.of(2024, 2, 1));
        assertThat(budget.getEndDate()).isEqualTo(LocalDate.of(2024, 2, 7));
        // Unchanged fields
        assertThat(budget.getId()).isEqualTo(1L);
        assertThat(budget.getUser()).isEqualTo(testUser);
        assertThat(budget.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should update Budget entity with partial fields from UpdateBudgetRequest")
    void shouldUpdateBudgetEntityWithPartialFieldsFromUpdateBudgetRequest() {
        // Given
        Budget budget = Budget.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("100.00"))
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .period(BudgetPeriod.MONTHLY)
                .isActive(true)
                .build();

        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .amount(new BigDecimal("250.00"))
                .period(BudgetPeriod.QUARTERLY)
                .build();

        // When
        budgetMapper.updateEntity(budget, request, null);

        // Then
        assertThat(budget.getAmount()).isEqualTo(new BigDecimal("250.00"));
        assertThat(budget.getPeriod()).isEqualTo(BudgetPeriod.QUARTERLY);
        // Unchanged fields
        assertThat(budget.getCategory()).isEqualTo(testCategory);
        assertThat(budget.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(budget.getEndDate()).isEqualTo(LocalDate.of(2024, 1, 31));
        assertThat(budget.getId()).isEqualTo(1L);
        assertThat(budget.getUser()).isEqualTo(testUser);
        assertThat(budget.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should not update Budget entity when all fields in UpdateBudgetRequest are null")
    void shouldNotUpdateBudgetEntityWhenAllFieldsInUpdateBudgetRequestAreNull() {
        // Given
        Budget originalBudget = Budget.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("100.00"))
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .period(BudgetPeriod.MONTHLY)
                .isActive(true)
                .build();

        Budget budget = Budget.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("100.00"))
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .period(BudgetPeriod.MONTHLY)
                .isActive(true)
                .build();

        UpdateBudgetRequest request = UpdateBudgetRequest.builder().build();

        // When
        budgetMapper.updateEntity(budget, request, null);

        // Then - All fields should remain unchanged
        assertThat(budget.getId()).isEqualTo(originalBudget.getId());
        assertThat(budget.getUser()).isEqualTo(originalBudget.getUser());
        assertThat(budget.getCategory()).isEqualTo(originalBudget.getCategory());
        assertThat(budget.getAmount()).isEqualTo(originalBudget.getAmount());
        assertThat(budget.getStartDate()).isEqualTo(originalBudget.getStartDate());
        assertThat(budget.getEndDate()).isEqualTo(originalBudget.getEndDate());
        assertThat(budget.getPeriod()).isEqualTo(originalBudget.getPeriod());
        assertThat(budget.getIsActive()).isEqualTo(originalBudget.getIsActive());
    }

    @Test
    @DisplayName("Should handle null Budget entity in updateEntity")
    void shouldHandleNullBudgetEntityInUpdateEntity() {
        // Given
        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .amount(new BigDecimal("100.00"))
                .build();

        // When & Then - Should not throw exception
        budgetMapper.updateEntity(null, request, testCategory);
    }

    @Test
    @DisplayName("Should handle null UpdateBudgetRequest in updateEntity")
    void shouldHandleNullUpdateBudgetRequestInUpdateEntity() {
        // Given
        Budget budget = Budget.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("100.00"))
                .build();

        // When & Then - Should not throw exception
        budgetMapper.updateEntity(budget, null, testCategory);
    }

    @Test
    @DisplayName("Should update category when new category is provided")
    void shouldUpdateCategoryWhenNewCategoryIsProvided() {
        // Given
        Budget budget = Budget.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("100.00"))
                .build();

        Category newCategory = Category.builder()
                .id(3L)
                .name("Entertainment")
                .user(testUser)
                .build();

        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .categoryId(3L)
                .build();

        // When
        budgetMapper.updateEntity(budget, request, newCategory);

        // Then
        assertThat(budget.getCategory()).isEqualTo(newCategory);
        assertThat(budget.getCategory().getName()).isEqualTo("Entertainment");
    }

    @Test
    @DisplayName("Should not update category when new category is null")
    void shouldNotUpdateCategoryWhenNewCategoryIsNull() {
        // Given
        Budget budget = Budget.builder()
                .id(1L)
                .user(testUser)
                .category(testCategory)
                .amount(new BigDecimal("100.00"))
                .build();

        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .categoryId(2L)
                .amount(new BigDecimal("200.00"))
                .build();

        // When
        budgetMapper.updateEntity(budget, request, null);

        // Then
        assertThat(budget.getCategory()).isEqualTo(testCategory); // Unchanged
        assertThat(budget.getAmount()).isEqualTo(new BigDecimal("200.00")); // Changed
    }

    @Test
    @DisplayName("Should test all BudgetPeriod values in mapping")
    void shouldTestAllBudgetPeriodValuesInMapping() {
        for (BudgetPeriod period : BudgetPeriod.values()) {
            // Given
            CreateBudgetRequest request = CreateBudgetRequest.builder()
                    .categoryId(1L)
                    .amount(new BigDecimal("100.00"))
                    .period(period)
                    .startDate(LocalDate.now())
                    .build();

            // When
            Budget budget = budgetMapper.toEntity(request, testUser, testCategory);

            // Then
            assertThat(budget.getPeriod()).isEqualTo(period);
        }
    }
}
