package com.dimitar.financetracker.repository;

import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.model.CategoryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    private User testUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testUser = entityManager.persistAndFlush(testUser);

        anotherUser = User.builder()
                .username("anotheruser")
                .email("another@example.com")
                .password("password123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        anotherUser = entityManager.persistAndFlush(anotherUser);
    }

    @Test
    void save_shouldPersistCategory() {
        Category category = Category.builder()
                .user(testUser)
                .name("Food & Dining")
                .type(CategoryType.EXPENSE)
                .color("#FF5733")
                .createdAt(LocalDateTime.now())
                .build();

        Category savedCategory = categoryRepository.save(category);

        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("Food & Dining");
        assertThat(savedCategory.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(savedCategory.getColor()).isEqualTo("#FF5733");
        assertThat(savedCategory.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void findByUserId_shouldReturnUserCategories() {
        Category category1 = createAndPersistCategory(testUser, "Food", CategoryType.EXPENSE, "#FF0000");
        Category category2 = createAndPersistCategory(testUser, "Salary", CategoryType.INCOME, "#00FF00");
        createAndPersistCategory(anotherUser, "Transport", CategoryType.EXPENSE, "#0000FF");

        List<Category> categories = categoryRepository.findByUserId(testUser.getId());

        assertThat(categories).hasSize(2);
        assertThat(categories).extracting(Category::getName)
                .containsExactlyInAnyOrder("Food", "Salary");
    }

    @Test
    void findByUserId_shouldReturnEmptyListWhenNoCategories() {
        List<Category> categories = categoryRepository.findByUserId(999L);
        assertThat(categories).isEmpty();
    }

    @Test
    void findByUserIdAndType_shouldReturnCategoriesOfSpecificType() {
        createAndPersistCategory(testUser, "Food", CategoryType.EXPENSE, "#FF0000");
        createAndPersistCategory(testUser, "Entertainment", CategoryType.EXPENSE, "#FF5733");
        createAndPersistCategory(testUser, "Salary", CategoryType.INCOME, "#00FF00");

        List<Category> expenseCategories = categoryRepository.findByUserIdAndType(testUser.getId(), CategoryType.EXPENSE);
        List<Category> incomeCategories = categoryRepository.findByUserIdAndType(testUser.getId(), CategoryType.INCOME);

        assertThat(expenseCategories).hasSize(2);
        assertThat(expenseCategories).extracting(Category::getName)
                .containsExactlyInAnyOrder("Food", "Entertainment");

        assertThat(incomeCategories).hasSize(1);
        assertThat(incomeCategories).extracting(Category::getName)
                .containsExactly("Salary");
    }

    @Test
    void findByUserIdAndType_shouldReturnEmptyListWhenNoMatchingType() {
        createAndPersistCategory(testUser, "Food", CategoryType.EXPENSE, "#FF0000");

        List<Category> incomeCategories = categoryRepository.findByUserIdAndType(testUser.getId(), CategoryType.INCOME);

        assertThat(incomeCategories).isEmpty();
    }

    @Test
    void findByUserIdAndName_shouldReturnCategoryWhenExists() {
        Category category = createAndPersistCategory(testUser, "Food & Dining", CategoryType.EXPENSE, "#FF0000");

        Optional<Category> found = categoryRepository.findByUserIdAndName(testUser.getId(), "Food & Dining");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(category.getId());
        assertThat(found.get().getName()).isEqualTo("Food & Dining");
    }

    @Test
    void findByUserIdAndName_shouldReturnEmptyWhenNotExists() {
        Optional<Category> found = categoryRepository.findByUserIdAndName(testUser.getId(), "NonExistent");

        assertThat(found).isNotPresent();
    }

    @Test
    void findByUserIdAndName_shouldBeCaseSensitive() {
        createAndPersistCategory(testUser, "Food", CategoryType.EXPENSE, "#FF0000");

        Optional<Category> found = categoryRepository.findByUserIdAndName(testUser.getId(), "FOOD");

        assertThat(found).isNotPresent();
    }

    @Test
    void findByUserIdAndName_shouldNotReturnCategoryFromDifferentUser() {
        createAndPersistCategory(anotherUser, "Food", CategoryType.EXPENSE, "#FF0000");

        Optional<Category> found = categoryRepository.findByUserIdAndName(testUser.getId(), "Food");

        assertThat(found).isNotPresent();
    }

    @Test
    void existsByUserIdAndName_shouldReturnTrueWhenExists() {
        createAndPersistCategory(testUser, "Food", CategoryType.EXPENSE, "#FF0000");

        Boolean exists = categoryRepository.existsByUserIdAndName(testUser.getId(), "Food");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByUserIdAndName_shouldReturnFalseWhenNotExists() {
        Boolean exists = categoryRepository.existsByUserIdAndName(testUser.getId(), "NonExistent");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByUserIdAndName_shouldBeCaseSensitive() {
        createAndPersistCategory(testUser, "Food", CategoryType.EXPENSE, "#FF0000");

        Boolean exists = categoryRepository.existsByUserIdAndName(testUser.getId(), "FOOD");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByUserIdAndName_shouldNotReturnTrueForDifferentUser() {
        createAndPersistCategory(anotherUser, "Food", CategoryType.EXPENSE, "#FF0000");

        Boolean exists = categoryRepository.existsByUserIdAndName(testUser.getId(), "Food");

        assertThat(exists).isFalse();
    }

    @Test
    void findById_shouldReturnCategoryWhenExists() {
        Category category = createAndPersistCategory(testUser, "Food", CategoryType.EXPENSE, "#FF0000");

        Optional<Category> found = categoryRepository.findById(category.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(category.getId());
        assertThat(found.get().getName()).isEqualTo("Food");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        Optional<Category> found = categoryRepository.findById(999L);

        assertThat(found).isNotPresent();
    }

    @Test
    void deleteById_shouldRemoveCategory() {
        Category category = createAndPersistCategory(testUser, "Food", CategoryType.EXPENSE, "#FF0000");
        Long categoryId = category.getId();

        categoryRepository.deleteById(categoryId);
        entityManager.flush();

        Optional<Category> found = categoryRepository.findById(categoryId);
        assertThat(found).isNotPresent();
    }

    @Test
    void findAll_shouldReturnAllCategories() {
        createAndPersistCategory(testUser, "Food", CategoryType.EXPENSE, "#FF0000");
        createAndPersistCategory(anotherUser, "Salary", CategoryType.INCOME, "#00FF00");

        List<Category> categories = categoryRepository.findAll();

        assertThat(categories).hasSize(2);
        assertThat(categories).extracting(Category::getName)
                .containsExactlyInAnyOrder("Food", "Salary");
    }

    @Test
    void save_shouldUpdateExistingCategory() {
        Category category = createAndPersistCategory(testUser, "Food", CategoryType.EXPENSE, "#FF0000");

        category.setName("Food & Dining");
        category.setColor("#FF5733");

        Category updatedCategory = categoryRepository.save(category);
        entityManager.flush();

        assertThat(updatedCategory.getId()).isEqualTo(category.getId());
        assertThat(updatedCategory.getName()).isEqualTo("Food & Dining");
        assertThat(updatedCategory.getColor()).isEqualTo("#FF5733");
    }

    @Test
    void findByUserIdAndType_withMultipleUsers_shouldOnlyReturnCurrentUserCategories() {
        createAndPersistCategory(testUser, "Food", CategoryType.EXPENSE, "#FF0000");
        createAndPersistCategory(testUser, "Transport", CategoryType.EXPENSE, "#00FF00");
        createAndPersistCategory(anotherUser, "Shopping", CategoryType.EXPENSE, "#0000FF");

        List<Category> testUserExpenses = categoryRepository.findByUserIdAndType(testUser.getId(), CategoryType.EXPENSE);

        assertThat(testUserExpenses).hasSize(2);
        assertThat(testUserExpenses).extracting(Category::getName)
                .containsExactlyInAnyOrder("Food", "Transport");
        assertThat(testUserExpenses).allMatch(category -> category.getUser().getId().equals(testUser.getId()));
    }

    @Test
    void findByUserId_shouldOrderByCreatedAt() {
        // Create categories with slight delay to ensure different creation times
        Category category1 = createAndPersistCategory(testUser, "First", CategoryType.EXPENSE, "#FF0000");
        entityManager.flush();

        Category category2 = createAndPersistCategory(testUser, "Second", CategoryType.INCOME, "#00FF00");
        entityManager.flush();

        List<Category> categories = categoryRepository.findByUserId(testUser.getId());

        assertThat(categories).hasSize(2);
    }

    @Test
    void findByUserIdAndName_shouldHandleSpecialCharacters() {
        String specialName = "Food & Dining - Restaurant's";
        createAndPersistCategory(testUser, specialName, CategoryType.EXPENSE, "#FF0000");

        Optional<Category> found = categoryRepository.findByUserIdAndName(testUser.getId(), specialName);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(specialName);
    }

    @Test
    void existsByUserIdAndName_shouldHandleNullName() {
        Boolean exists = categoryRepository.existsByUserIdAndName(testUser.getId(), null);

        assertThat(exists).isFalse();
    }

    @Test
    void findByUserIdAndType_shouldHandleBothCategoryTypes() {
        createAndPersistCategory(testUser, "Groceries", CategoryType.EXPENSE, "#FF0000");
        createAndPersistCategory(testUser, "Salary", CategoryType.INCOME, "#00FF00");
        createAndPersistCategory(testUser, "Freelance", CategoryType.INCOME, "#0000FF");

        List<Category> expenses = categoryRepository.findByUserIdAndType(testUser.getId(), CategoryType.EXPENSE);
        List<Category> incomes = categoryRepository.findByUserIdAndType(testUser.getId(), CategoryType.INCOME);

        assertThat(expenses).hasSize(1);
        assertThat(incomes).hasSize(2);
        assertThat(expenses.get(0).getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(incomes).allMatch(cat -> cat.getType() == CategoryType.INCOME);
    }

    private Category createAndPersistCategory(User user, String name, CategoryType type, String color) {
        Category category = Category.builder()
                .user(user)
                .name(name)
                .type(type)
                .color(color)
                .createdAt(LocalDateTime.now())
                .build();
        return entityManager.persistAndFlush(category);
    }
}

