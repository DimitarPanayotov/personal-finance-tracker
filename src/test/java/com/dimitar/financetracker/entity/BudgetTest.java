package com.dimitar.financetracker.entity;

import com.dimitar.financetracker.model.BudgetPeriod;
import com.dimitar.financetracker.model.CategoryType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetTest {

    private static Validator validator;
    private Budget budget;
    private User user;
    private Category category;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        category = Category.builder()
                .id(1L)
                .user(user)
                .name("Food & Dining")
                .type(CategoryType.EXPENSE)
                .color("#FF5733")
                .build();

        budget = Budget.builder()
                .user(user)
                .category(category)
                .amount(new BigDecimal("500.00"))
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .period(BudgetPeriod.MONTHLY)
                .isActive(true)
                .build();
    }

    @Test
    void validBudget_shouldPassValidation() {
        Set<ConstraintViolation<Budget>> violations = validator.validate(budget);
        assertThat(violations).isEmpty();
    }

    @Test
    void builder_shouldCreateBudgetWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        Budget budget = Budget.builder()
                .id(1L)
                .user(user)
                .category(category)
                .amount(new BigDecimal("1000.00"))
                .startDate(startDate)
                .endDate(endDate)
                .period(BudgetPeriod.MONTHLY)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(budget.getId()).isEqualTo(1L);
        assertThat(budget.getUser()).isEqualTo(user);
        assertThat(budget.getCategory()).isEqualTo(category);
        assertThat(budget.getAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(budget.getStartDate()).isEqualTo(startDate);
        assertThat(budget.getEndDate()).isEqualTo(endDate);
        assertThat(budget.getPeriod()).isEqualTo(BudgetPeriod.MONTHLY);
        assertThat(budget.getIsActive()).isTrue();
        assertThat(budget.getCreatedAt()).isEqualTo(now);
        assertThat(budget.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void nullUser_shouldFailValidation() {
        budget.setUser(null);

        Set<ConstraintViolation<Budget>> violations = validator.validate(budget);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("user"));
    }

    @Test
    void nullCategory_shouldFailValidation() {
        budget.setCategory(null);

        Set<ConstraintViolation<Budget>> violations = validator.validate(budget);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("category"));
    }

    @Test
    void nullAmount_shouldFailValidation() {
        budget.setAmount(null);

        Set<ConstraintViolation<Budget>> violations = validator.validate(budget);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    @Test
    void zeroAmount_shouldFailValidation() {
        budget.setAmount(BigDecimal.ZERO);

        Set<ConstraintViolation<Budget>> violations = validator.validate(budget);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    @Test
    void negativeAmount_shouldFailValidation() {
        budget.setAmount(new BigDecimal("-100.00"));

        Set<ConstraintViolation<Budget>> violations = validator.validate(budget);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    @Test
    void validMinimumAmount_shouldPassValidation() {
        budget.setAmount(new BigDecimal("0.01"));

        Set<ConstraintViolation<Budget>> violations = validator.validate(budget);
        assertThat(violations)
                .filteredOn(v -> v.getPropertyPath().toString().equals("amount"))
                .isEmpty();
    }

    @Test
    void nullStartDate_shouldFailValidation() {
        budget.setStartDate(null);

        Set<ConstraintViolation<Budget>> violations = validator.validate(budget);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("startDate"));
    }

    @Test
    void nullEndDate_shouldFailValidation() {
        budget.setEndDate(null);

        Set<ConstraintViolation<Budget>> violations = validator.validate(budget);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("endDate"));
    }

    @Test
    void nullPeriod_shouldFailValidation() {
        budget.setPeriod(null);

        Set<ConstraintViolation<Budget>> violations = validator.validate(budget);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("period"));
    }

    @Test
    void validBudgetPeriods_shouldPassValidation() {
        BudgetPeriod[] periods = {BudgetPeriod.WEEKLY, BudgetPeriod.MONTHLY,
                                  BudgetPeriod.QUARTERLY, BudgetPeriod.YEARLY, BudgetPeriod.CUSTOM};

        for (BudgetPeriod period : periods) {
            budget.setPeriod(period);
            Set<ConstraintViolation<Budget>> violations = validator.validate(budget);
            assertThat(violations)
                .filteredOn(v -> v.getPropertyPath().toString().equals("period"))
                .isEmpty();
        }
    }

    @Test
    void onCreate_shouldSetTimestamps() {
        Budget newBudget = new Budget();
        newBudget.setStartDate(LocalDate.now());
        newBudget.setPeriod(BudgetPeriod.MONTHLY);
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        newBudget.onCreate();

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertThat(newBudget.getCreatedAt()).isBetween(before, after);
        assertThat(newBudget.getUpdatedAt()).isBetween(before, after);
        assertThat(newBudget.getCreatedAt()).isEqualTo(newBudget.getUpdatedAt());
    }

    @Test
    void onCreate_shouldCalculateEndDateForWeeklyPeriod() {
        Budget newBudget = new Budget();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        newBudget.setStartDate(startDate);
        newBudget.setPeriod(BudgetPeriod.WEEKLY);

        newBudget.onCreate();

        assertThat(newBudget.getEndDate()).isEqualTo(startDate.plusWeeks(1));
    }

    @Test
    void onCreate_shouldCalculateEndDateForMonthlyPeriod() {
        Budget newBudget = new Budget();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        newBudget.setStartDate(startDate);
        newBudget.setPeriod(BudgetPeriod.MONTHLY);

        newBudget.onCreate();

        assertThat(newBudget.getEndDate()).isEqualTo(startDate.plusMonths(1));
    }

    @Test
    void onCreate_shouldCalculateEndDateForQuarterlyPeriod() {
        Budget newBudget = new Budget();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        newBudget.setStartDate(startDate);
        newBudget.setPeriod(BudgetPeriod.QUARTERLY);

        newBudget.onCreate();

        assertThat(newBudget.getEndDate()).isEqualTo(startDate.plusMonths(3));
    }

    @Test
    void onCreate_shouldCalculateEndDateForYearlyPeriod() {
        Budget newBudget = new Budget();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        newBudget.setStartDate(startDate);
        newBudget.setPeriod(BudgetPeriod.YEARLY);

        newBudget.onCreate();

        assertThat(newBudget.getEndDate()).isEqualTo(startDate.plusYears(1));
    }

    @Test
    void onCreate_shouldNotCalculateEndDateForCustomPeriod() {
        Budget newBudget = new Budget();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate customEndDate = LocalDate.of(2024, 3, 15);
        newBudget.setStartDate(startDate);
        newBudget.setEndDate(customEndDate);
        newBudget.setPeriod(BudgetPeriod.CUSTOM);

        newBudget.onCreate();

        assertThat(newBudget.getEndDate()).isEqualTo(customEndDate);
    }

    @Test
    void onUpdate_shouldUpdateTimestamp() throws InterruptedException {
        budget.onCreate();
        LocalDateTime originalCreatedAt = budget.getCreatedAt();
        LocalDateTime originalUpdatedAt = budget.getUpdatedAt();

        Thread.sleep(10); // Small delay to ensure different timestamps
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        budget.onUpdate();

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertThat(budget.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(budget.getUpdatedAt()).isBetween(before, after);
        assertThat(budget.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void isCurrentlyActive_shouldReturnTrueWhenActiveAndWithinDateRange() {
        LocalDate today = LocalDate.now();
        budget.setStartDate(today.minusDays(5));
        budget.setEndDate(today.plusDays(5));
        budget.setIsActive(true);

        assertThat(budget.isCurrentlyActive()).isTrue();
    }

    @Test
    void isCurrentlyActive_shouldReturnFalseWhenInactive() {
        LocalDate today = LocalDate.now();
        budget.setStartDate(today.minusDays(5));
        budget.setEndDate(today.plusDays(5));
        budget.setIsActive(false);

        assertThat(budget.isCurrentlyActive()).isFalse();
    }

    @Test
    void isCurrentlyActive_shouldReturnFalseWhenBeforeStartDate() {
        LocalDate today = LocalDate.now();
        budget.setStartDate(today.plusDays(1));
        budget.setEndDate(today.plusDays(10));
        budget.setIsActive(true);

        assertThat(budget.isCurrentlyActive()).isFalse();
    }

    @Test
    void isCurrentlyActive_shouldReturnFalseWhenAfterEndDate() {
        LocalDate today = LocalDate.now();
        budget.setStartDate(today.minusDays(10));
        budget.setEndDate(today.minusDays(1));
        budget.setIsActive(true);

        assertThat(budget.isCurrentlyActive()).isFalse();
    }

    @Test
    void isCurrentlyActive_shouldReturnTrueWhenTodayIsStartDate() {
        LocalDate today = LocalDate.now();
        budget.setStartDate(today);
        budget.setEndDate(today.plusDays(10));
        budget.setIsActive(true);

        assertThat(budget.isCurrentlyActive()).isTrue();
    }

    @Test
    void isCurrentlyActive_shouldReturnTrueWhenTodayIsEndDate() {
        LocalDate today = LocalDate.now();
        budget.setStartDate(today.minusDays(10));
        budget.setEndDate(today);
        budget.setIsActive(true);

        assertThat(budget.isCurrentlyActive()).isTrue();
    }

    @Test
    void equals_shouldWorkCorrectlyWithSameData() {
        Budget budget1 = Budget.builder()
                .id(1L)
                .user(user)
                .category(category)
                .amount(new BigDecimal("500.00"))
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .period(BudgetPeriod.MONTHLY)
                .build();

        Budget budget2 = Budget.builder()
                .id(1L)
                .user(user)
                .category(category)
                .amount(new BigDecimal("500.00"))
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .period(BudgetPeriod.MONTHLY)
                .build();

        assertThat(budget1).isEqualTo(budget2);
        assertThat(budget1.hashCode()).isEqualTo(budget2.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentData() {
        Budget budget1 = Budget.builder()
                .id(1L)
                .amount(new BigDecimal("500.00"))
                .period(BudgetPeriod.MONTHLY)
                .build();

        Budget budget2 = Budget.builder()
                .id(2L)
                .amount(new BigDecimal("750.00"))
                .period(BudgetPeriod.WEEKLY)
                .build();

        assertThat(budget1).isNotEqualTo(budget2);
    }

    @Test
    void toString_shouldNotIncludeRelationships() {
        String toString = budget.toString();

        assertThat(toString).contains("amount=500.00");
        assertThat(toString).contains("period=MONTHLY");
        assertThat(toString).contains("isActive=true");
        assertThat(toString).doesNotContain("user");
        assertThat(toString).doesNotContain("category");
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyBudget() {
        Budget emptyBudget = new Budget();

        assertThat(emptyBudget.getId()).isNull();
        assertThat(emptyBudget.getUser()).isNull();
        assertThat(emptyBudget.getCategory()).isNull();
        assertThat(emptyBudget.getAmount()).isNull();
        assertThat(emptyBudget.getStartDate()).isNull();
        assertThat(emptyBudget.getEndDate()).isNull();
        assertThat(emptyBudget.getPeriod()).isNull();
        assertThat(emptyBudget.getIsActive()).isTrue(); // Default value
        assertThat(emptyBudget.getCreatedAt()).isNull();
        assertThat(emptyBudget.getUpdatedAt()).isNull();
    }

    @Test
    void validLargeAmount_shouldPassValidation() {
        budget.setAmount(new BigDecimal("999999.99"));

        Set<ConstraintViolation<Budget>> violations = validator.validate(budget);
        assertThat(violations)
                .filteredOn(v -> v.getPropertyPath().toString().equals("amount"))
                .isEmpty();
    }
}

