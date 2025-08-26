package com.dimitar.financetracker.repository;

import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.model.BudgetPeriod;
import com.dimitar.financetracker.model.CategoryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BudgetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BudgetRepository budgetRepository;

    private User testUser;
    private User anotherUser;
    private Category expenseCategory;
    private Category incomeCategory;

    @BeforeEach
    void setUp() {
        testUser = createAndPersistUser("testuser", "test@example.com");
        anotherUser = createAndPersistUser("anotheruser", "another@example.com");

        expenseCategory = createAndPersistCategory(testUser, "Food", CategoryType.EXPENSE);
        incomeCategory = createAndPersistCategory(testUser, "Salary", CategoryType.INCOME);
    }

    @Test
    void save_shouldPersistBudget() {
        Budget budget = Budget.builder()
                .user(testUser)
                .category(expenseCategory)
                .amount(new BigDecimal("500.00"))
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .period(BudgetPeriod.MONTHLY)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Budget savedBudget = budgetRepository.save(budget);

        assertThat(savedBudget.getId()).isNotNull();
        assertThat(savedBudget.getAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(savedBudget.getPeriod()).isEqualTo(BudgetPeriod.MONTHLY);
        assertThat(savedBudget.getIsActive()).isTrue();
        assertThat(savedBudget.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedBudget.getCategory().getId()).isEqualTo(expenseCategory.getId());
    }

    @Test
    void findByUserId_shouldReturnUserBudgets() {
        createAndPersistBudget(testUser, expenseCategory, "500.00", BudgetPeriod.MONTHLY, true);
        createAndPersistBudget(testUser, incomeCategory, "1000.00", BudgetPeriod.WEEKLY, false);
        createAndPersistBudget(anotherUser, expenseCategory, "300.00", BudgetPeriod.MONTHLY, true);

        List<Budget> budgets = budgetRepository.findByUserId(testUser.getId());

        assertThat(budgets).hasSize(2);
        assertThat(budgets).extracting(Budget::getAmount)
                .containsExactlyInAnyOrder(new BigDecimal("500.00"), new BigDecimal("1000.00"));
    }

    @Test
    void findByUserId_shouldReturnEmptyListWhenNoBudgets() {
        List<Budget> budgets = budgetRepository.findByUserId(999L);
        assertThat(budgets).isEmpty();
    }

    @Test
    void findByUserIdAndIsActiveTrue_shouldReturnOnlyActiveBudgets() {
        createAndPersistBudget(testUser, expenseCategory, "500.00", BudgetPeriod.MONTHLY, true);
        createAndPersistBudget(testUser, incomeCategory, "1000.00", BudgetPeriod.WEEKLY, false);
        createAndPersistBudget(testUser, expenseCategory, "750.00", BudgetPeriod.QUARTERLY, true);

        List<Budget> activeBudgets = budgetRepository.findByUserIdAndIsActiveTrue(testUser.getId());

        assertThat(activeBudgets).hasSize(2);
        assertThat(activeBudgets).extracting(Budget::getAmount)
                .containsExactlyInAnyOrder(new BigDecimal("500.00"), new BigDecimal("750.00"));
        assertThat(activeBudgets).allMatch(Budget::getIsActive);
    }

    @Test
    void findByUserIdAndIsActiveTrue_shouldReturnEmptyListWhenNoActiveBudgets() {
        createAndPersistBudget(testUser, expenseCategory, "500.00", BudgetPeriod.MONTHLY, false);

        List<Budget> activeBudgets = budgetRepository.findByUserIdAndIsActiveTrue(testUser.getId());

        assertThat(activeBudgets).isEmpty();
    }

    @Test
    void findByUserIdAndCategoryId_shouldReturnBudgetsForSpecificCategory() {
        createAndPersistBudget(testUser, expenseCategory, "500.00", BudgetPeriod.MONTHLY, true);
        createAndPersistBudget(testUser, incomeCategory, "1000.00", BudgetPeriod.WEEKLY, true);
        createAndPersistBudget(testUser, expenseCategory, "750.00", BudgetPeriod.QUARTERLY, false);

        List<Budget> expenseBudgets = budgetRepository.findByUserIdAndCategoryId(
                testUser.getId(), expenseCategory.getId());

        assertThat(expenseBudgets).hasSize(2);
        assertThat(expenseBudgets).extracting(Budget::getAmount)
                .containsExactlyInAnyOrder(new BigDecimal("500.00"), new BigDecimal("750.00"));
        assertThat(expenseBudgets).allMatch(budget ->
                budget.getCategory().getId().equals(expenseCategory.getId()));
    }

    @Test
    void findByUserIdAndCategoryId_shouldReturnEmptyListWhenNoBudgetsForCategory() {
        List<Budget> budgets = budgetRepository.findByUserIdAndCategoryId(testUser.getId(), 999L);
        assertThat(budgets).isEmpty();
    }

    @Test
    void findByUserIdAndPeriod_shouldReturnBudgetsForSpecificPeriod() {
        createAndPersistBudget(testUser, expenseCategory, "500.00", BudgetPeriod.MONTHLY, true);
        createAndPersistBudget(testUser, incomeCategory, "1000.00", BudgetPeriod.WEEKLY, true);
        createAndPersistBudget(testUser, expenseCategory, "750.00", BudgetPeriod.MONTHLY, false);

        List<Budget> monthlyBudgets = budgetRepository.findByUserIdAndPeriod(
                testUser.getId(), BudgetPeriod.MONTHLY);

        assertThat(monthlyBudgets).hasSize(2);
        assertThat(monthlyBudgets).extracting(Budget::getAmount)
                .containsExactlyInAnyOrder(new BigDecimal("500.00"), new BigDecimal("750.00"));
        assertThat(monthlyBudgets).allMatch(budget ->
                budget.getPeriod() == BudgetPeriod.MONTHLY);
    }

    @Test
    void findByUserIdAndPeriod_shouldReturnEmptyListWhenNoBudgetsForPeriod() {
        createAndPersistBudget(testUser, expenseCategory, "500.00", BudgetPeriod.MONTHLY, true);

        List<Budget> weeklyBudgets = budgetRepository.findByUserIdAndPeriod(
                testUser.getId(), BudgetPeriod.WEEKLY);

        assertThat(weeklyBudgets).isEmpty();
    }

    @Test
    void findById_shouldReturnBudgetWhenExists() {
        Budget budget = createAndPersistBudget(testUser, expenseCategory, "500.00", BudgetPeriod.MONTHLY, true);

        Optional<Budget> found = budgetRepository.findById(budget.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(budget.getId());
        assertThat(found.get().getAmount()).isEqualTo(new BigDecimal("500.00"));
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        Optional<Budget> found = budgetRepository.findById(999L);

        assertThat(found).isNotPresent();
    }

    @Test
    void deleteById_shouldRemoveBudget() {
        Budget budget = createAndPersistBudget(testUser, expenseCategory, "500.00", BudgetPeriod.MONTHLY, true);
        Long budgetId = budget.getId();

        budgetRepository.deleteById(budgetId);
        entityManager.flush();

        Optional<Budget> found = budgetRepository.findById(budgetId);
        assertThat(found).isNotPresent();
    }

    @Test
    void save_shouldUpdateExistingBudget() {
        Budget budget = createAndPersistBudget(testUser, expenseCategory, "500.00", BudgetPeriod.MONTHLY, true);

        budget.setAmount(new BigDecimal("750.00"));
        budget.setPeriod(BudgetPeriod.WEEKLY);
        budget.setIsActive(false);

        Budget updatedBudget = budgetRepository.save(budget);
        entityManager.flush();

        assertThat(updatedBudget.getId()).isEqualTo(budget.getId());
        assertThat(updatedBudget.getAmount()).isEqualTo(new BigDecimal("750.00"));
        assertThat(updatedBudget.getPeriod()).isEqualTo(BudgetPeriod.WEEKLY);
        assertThat(updatedBudget.getIsActive()).isFalse();
    }

    @Test
    void findAll_shouldReturnAllBudgets() {
        createAndPersistBudget(testUser, expenseCategory, "500.00", BudgetPeriod.MONTHLY, true);
        createAndPersistBudget(anotherUser, incomeCategory, "1000.00", BudgetPeriod.WEEKLY, true);

        List<Budget> budgets = budgetRepository.findAll();

        assertThat(budgets).hasSize(2);
        assertThat(budgets).extracting(Budget::getAmount)
                .containsExactlyInAnyOrder(new BigDecimal("500.00"), new BigDecimal("1000.00"));
    }

    @Test
    void findByUserIdAndIsActiveTrue_shouldNotReturnBudgetsFromDifferentUser() {
        createAndPersistBudget(testUser, expenseCategory, "500.00", BudgetPeriod.MONTHLY, true);
        createAndPersistBudget(anotherUser, expenseCategory, "1000.00", BudgetPeriod.WEEKLY, true);

        List<Budget> testUserBudgets = budgetRepository.findByUserIdAndIsActiveTrue(testUser.getId());

        assertThat(testUserBudgets).hasSize(1);
        assertThat(testUserBudgets.get(0).getUser().getId()).isEqualTo(testUser.getId());
        assertThat(testUserBudgets.get(0).getAmount()).isEqualTo(new BigDecimal("500.00"));
    }

    @Test
    void findByUserIdAndCategoryId_shouldNotReturnBudgetsFromDifferentUser() {
        Category anotherUserCategory = createAndPersistCategory(anotherUser, "Transport", CategoryType.EXPENSE);

        createAndPersistBudget(testUser, expenseCategory, "500.00", BudgetPeriod.MONTHLY, true);
        createAndPersistBudget(anotherUser, anotherUserCategory, "1000.00", BudgetPeriod.WEEKLY, true);

        List<Budget> budgets = budgetRepository.findByUserIdAndCategoryId(
                testUser.getId(), expenseCategory.getId());

        assertThat(budgets).hasSize(1);
        assertThat(budgets.get(0).getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void findByUserIdAndPeriod_shouldHandleAllBudgetPeriods() {
        createAndPersistBudget(testUser, expenseCategory, "100.00", BudgetPeriod.WEEKLY, true);
        createAndPersistBudget(testUser, expenseCategory, "500.00", BudgetPeriod.MONTHLY, true);
        createAndPersistBudget(testUser, expenseCategory, "1500.00", BudgetPeriod.QUARTERLY, true);
        createAndPersistBudget(testUser, expenseCategory, "6000.00", BudgetPeriod.YEARLY, true);
        createAndPersistBudget(testUser, expenseCategory, "2000.00", BudgetPeriod.CUSTOM, true);

        List<Budget> weeklyBudgets = budgetRepository.findByUserIdAndPeriod(testUser.getId(), BudgetPeriod.WEEKLY);
        List<Budget> monthlyBudgets = budgetRepository.findByUserIdAndPeriod(testUser.getId(), BudgetPeriod.MONTHLY);
        List<Budget> quarterlyBudgets = budgetRepository.findByUserIdAndPeriod(testUser.getId(), BudgetPeriod.QUARTERLY);
        List<Budget> yearlyBudgets = budgetRepository.findByUserIdAndPeriod(testUser.getId(), BudgetPeriod.YEARLY);
        List<Budget> customBudgets = budgetRepository.findByUserIdAndPeriod(testUser.getId(), BudgetPeriod.CUSTOM);

        assertThat(weeklyBudgets).hasSize(1);
        assertThat(monthlyBudgets).hasSize(1);
        assertThat(quarterlyBudgets).hasSize(1);
        assertThat(yearlyBudgets).hasSize(1);
        assertThat(customBudgets).hasSize(1);

        assertThat(weeklyBudgets.get(0).getAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(monthlyBudgets.get(0).getAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(quarterlyBudgets.get(0).getAmount()).isEqualTo(new BigDecimal("1500.00"));
        assertThat(yearlyBudgets.get(0).getAmount()).isEqualTo(new BigDecimal("6000.00"));
        assertThat(customBudgets.get(0).getAmount()).isEqualTo(new BigDecimal("2000.00"));
    }

    private User createAndPersistUser(String username, String email) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password("password123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return entityManager.persistAndFlush(user);
    }

    private Category createAndPersistCategory(User user, String name, CategoryType type) {
        Category category = Category.builder()
                .user(user)
                .name(name)
                .type(type)
                .color("#FF0000")
                .createdAt(LocalDateTime.now())
                .build();
        return entityManager.persistAndFlush(category);
    }

    private Budget createAndPersistBudget(User user, Category category, String amount,
                                         BudgetPeriod period, Boolean isActive) {
        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .amount(new BigDecimal(amount))
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .period(period)
                .isActive(isActive)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return entityManager.persistAndFlush(budget);
    }
}

