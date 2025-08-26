package com.dimitar.financetracker.entity;

import com.dimitar.financetracker.model.CategoryType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    private static Validator validator;
    private Category category;
    private User user;

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
                .user(user)
                .name("Food & Dining")
                .type(CategoryType.EXPENSE)
                .color("#FF5733")
                .build();
    }

    @Test
    void validCategory_shouldPassValidation() {
        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertThat(violations).isEmpty();
    }

    @Test
    void builder_shouldCreateCategoryWithAllFields() {
        LocalDateTime now = LocalDateTime.now();

        Category category = Category.builder()
                .id(1L)
                .user(user)
                .name("Entertainment")
                .type(CategoryType.EXPENSE)
                .color("#00FF00")
                .createdAt(now)
                .build();

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getUser()).isEqualTo(user);
        assertThat(category.getName()).isEqualTo("Entertainment");
        assertThat(category.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(category.getColor()).isEqualTo("#00FF00");
        assertThat(category.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void nullUser_shouldFailValidation() {
        category.setUser(null);

        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("user"));
    }

    @Test
    void blankName_shouldFailValidation() {
        category.setName("");

        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void nullName_shouldFailValidation() {
        category.setName(null);

        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void tooLongName_shouldFailValidation() {
        String longName = "a".repeat(101); // assuming CATEGORY_NAME_MAX_LENGTH is 100
        category.setName(longName);

        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void nullType_shouldFailValidation() {
        category.setType(null);

        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("type"));
    }

    @Test
    void blankColor_shouldFailValidation() {
        category.setColor("");

        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
    }

    @Test
    void nullColor_shouldFailValidation() {
        category.setColor(null);

        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
    }

    @Test
    void tooLongColor_shouldFailValidation() {
        String longColor = "a".repeat(8);
        category.setColor(longColor);

        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
    }

    @Test
    void validCategoryTypes_shouldPassValidation() {
        CategoryType[] types = {CategoryType.INCOME, CategoryType.EXPENSE};

        for (CategoryType type : types) {
            category.setType(type);
            Set<ConstraintViolation<Category>> violations = validator.validate(category);
            assertThat(violations)
                .filteredOn(v -> v.getPropertyPath().toString().equals("type"))
                .isEmpty();
        }
    }

    @Test
    void validColorFormats_shouldPassValidation() {
        String[] validColors = {"#FF0000", "#00FF00", "#0000FF", "#FFFFFF", "#000000"};

        for (String color : validColors) {
            category.setColor(color);
            Set<ConstraintViolation<Category>> violations = validator.validate(category);
            assertThat(violations)
                .filteredOn(v -> v.getPropertyPath().toString().equals("color"))
                .isEmpty();
        }
    }

    @Test
    void onCreate_shouldSetTimestamp() {
        Category newCategory = new Category();
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        newCategory.onCreate();

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertThat(newCategory.getCreatedAt()).isBetween(before, after);
    }

    @Test
    void equals_shouldWorkCorrectlyWithSameData() {
        Category category1 = Category.builder()
                .id(1L)
                .user(user)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        Category category2 = Category.builder()
                .id(1L)
                .user(user)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        assertThat(category1).isEqualTo(category2);
        assertThat(category1.hashCode()).isEqualTo(category2.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentData() {
        Category category1 = Category.builder()
                .id(1L)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        Category category2 = Category.builder()
                .id(2L)
                .name("Transportation")
                .type(CategoryType.EXPENSE)
                .color("#00FF00")
                .build();

        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    void toString_shouldNotIncludeUser() {
        String toString = category.toString();

        assertThat(toString).contains("name=Food & Dining");
        assertThat(toString).contains("type=EXPENSE");
        assertThat(toString).contains("color=#FF5733");
        assertThat(toString).doesNotContain("user");
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyCategory() {
        Category emptyCategory = new Category();

        assertThat(emptyCategory.getId()).isNull();
        assertThat(emptyCategory.getUser()).isNull();
        assertThat(emptyCategory.getName()).isNull();
        assertThat(emptyCategory.getType()).isNull();
        assertThat(emptyCategory.getColor()).isNull();
        assertThat(emptyCategory.getCreatedAt()).isNull();
    }

    @Test
    void whitespaceOnlyName_shouldFailValidation() {
        category.setName("   ");

        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void whitespaceOnlyColor_shouldFailValidation() {
        category.setColor("   ");

        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
    }
}

