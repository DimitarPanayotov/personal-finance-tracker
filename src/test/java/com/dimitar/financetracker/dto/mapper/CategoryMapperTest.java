package com.dimitar.financetracker.dto.mapper;

import com.dimitar.financetracker.dto.request.category.CreateCategoryRequest;
import com.dimitar.financetracker.dto.request.category.UpdateCategoryRequest;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.dto.response.category.CategorySummaryResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.model.CategoryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperTest {

    private CategoryMapper mapper;
    private User testUser;

    @BeforeEach
    void setUp() {
        mapper = new CategoryMapper();
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
    }

    @Test
    void toEntity_shouldMapFieldsCorrectly() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Food & Dining")
                .type(CategoryType.EXPENSE)
                .color("#FF5733")
                .build();

        Category category = mapper.toEntity(request, testUser);

        assertThat(category).isNotNull();
        assertThat(category.getUser()).isEqualTo(testUser);
        assertThat(category.getName()).isEqualTo("Food & Dining");
        assertThat(category.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(category.getColor()).isEqualTo("#FF5733");
        assertThat(category.getId()).isNull();
        assertThat(category.getCreatedAt()).isNull();
        assertThat(category.getUpdatedAt()).isNull();
    }

    @Test
    void toEntity_withNullRequest_shouldReturnNull() {
        Category category = mapper.toEntity(null, testUser);
        assertThat(category).isNull();
    }

    @Test
    void toEntity_withNullUser_shouldCreateCategoryWithNullUser() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        Category category = mapper.toEntity(request, null);

        assertThat(category).isNotNull();
        assertThat(category.getUser()).isNull();
        assertThat(category.getName()).isEqualTo("Food");
        assertThat(category.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(category.getColor()).isEqualTo("#FF0000");
    }

    @Test
    void toResponse_shouldMapFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        Category category = Category.builder()
                .id(1L)
                .user(testUser)
                .name("Transportation")
                .type(CategoryType.EXPENSE)
                .color("#0000FF")
                .createdAt(now)
                .updatedAt(now)
                .build();

        CategoryResponse response = mapper.toResponse(category);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Transportation");
        assertThat(response.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(response.getColor()).isEqualTo("#0000FF");
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toResponse_withNullCategory_shouldReturnNull() {
        CategoryResponse response = mapper.toResponse(null);
        assertThat(response).isNull();
    }

    @Test
    void toSummaryResponse_shouldMapFieldsCorrectly() {
        Category category = Category.builder()
                .id(2L)
                .user(testUser)
                .name("Entertainment")
                .type(CategoryType.EXPENSE)
                .color("#00FF00")
                .build();

        CategorySummaryResponse response = mapper.toSummaryResponse(category);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getName()).isEqualTo("Entertainment");
        assertThat(response.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(response.getColor()).isEqualTo("#00FF00");
    }

    @Test
    void toSummaryResponse_withNullCategory_shouldReturnNull() {
        CategorySummaryResponse response = mapper.toSummaryResponse(null);
        assertThat(response).isNull();
    }

    @Test
    void updateEntity_shouldUpdateAllFields() {
        Category category = Category.builder()
                .id(1L)
                .user(testUser)
                .name("Old Name")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name("New Name")
                .type(CategoryType.INCOME)
                .color("#00FF00")
                .build();

        LocalDateTime beforeUpdate = LocalDateTime.now();
        mapper.updateEntity(category, request);
        LocalDateTime afterUpdate = LocalDateTime.now();

        assertThat(category.getName()).isEqualTo("New Name");
        assertThat(category.getType()).isEqualTo(CategoryType.INCOME);
        assertThat(category.getColor()).isEqualTo("#00FF00");
        assertThat(category.getUpdatedAt()).isBetween(beforeUpdate, afterUpdate);
    }

    @Test
    void updateEntity_shouldUpdateOnlyProvidedFields() {
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusDays(1);
        Category category = Category.builder()
                .id(1L)
                .user(testUser)
                .name("Original Name")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(originalUpdatedAt)
                .build();

        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name("Updated Name")
                // type and color are null
                .build();

        LocalDateTime beforeUpdate = LocalDateTime.now();
        mapper.updateEntity(category, request);
        LocalDateTime afterUpdate = LocalDateTime.now();

        assertThat(category.getName()).isEqualTo("Updated Name");
        assertThat(category.getType()).isEqualTo(CategoryType.EXPENSE); // unchanged
        assertThat(category.getColor()).isEqualTo("#FF0000"); // unchanged
        assertThat(category.getUpdatedAt()).isBetween(beforeUpdate, afterUpdate);
    }

    @Test
    void updateEntity_shouldNotUpdateWhenNoChanges() {
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusDays(1);
        Category category = Category.builder()
                .id(1L)
                .user(testUser)
                .name("Original Name")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(originalUpdatedAt)
                .build();

        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name(null)
                .type(null)
                .color(null)
                .build();

        mapper.updateEntity(category, request);

        assertThat(category.getName()).isEqualTo("Original Name");
        assertThat(category.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(category.getColor()).isEqualTo("#FF0000");
        assertThat(category.getUpdatedAt()).isEqualTo(originalUpdatedAt); // unchanged
    }

    @Test
    void updateEntity_withNullCategory_shouldNotThrow() {
        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name("New Name")
                .type(CategoryType.INCOME)
                .color("#00FF00")
                .build();

        // Should not throw exception
        mapper.updateEntity(null, request);
    }

    @Test
    void updateEntity_withNullRequest_shouldNotThrow() {
        Category category = Category.builder()
                .id(1L)
                .name("Original Name")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        // Should not throw exception
        mapper.updateEntity(category, null);

        // Category should remain unchanged
        assertThat(category.getName()).isEqualTo("Original Name");
        assertThat(category.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(category.getColor()).isEqualTo("#FF0000");
    }

    @Test
    void updateEntity_shouldHandleEmptyStrings() {
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusDays(1);
        Category category = Category.builder()
                .id(1L)
                .user(testUser)
                .name("Original Name")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .updatedAt(originalUpdatedAt)
                .build();

        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name("")
                .color("")
                .build();

        LocalDateTime beforeUpdate = LocalDateTime.now();
        mapper.updateEntity(category, request);
        LocalDateTime afterUpdate = LocalDateTime.now();

        assertThat(category.getName()).isEqualTo("");
        assertThat(category.getColor()).isEqualTo("");
        assertThat(category.getType()).isEqualTo(CategoryType.EXPENSE); // unchanged
        assertThat(category.getUpdatedAt()).isBetween(beforeUpdate, afterUpdate);
    }

    @Test
    void updateEntity_shouldUpdateEachFieldIndependently() {
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusDays(1);
        Category category = Category.builder()
                .id(1L)
                .user(testUser)
                .name("Original Name")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .updatedAt(originalUpdatedAt)
                .build();

        // Test updating only name
        UpdateCategoryRequest nameOnlyRequest = UpdateCategoryRequest.builder()
                .name("Name Only Update")
                .build();
        mapper.updateEntity(category, nameOnlyRequest);
        assertThat(category.getName()).isEqualTo("Name Only Update");
        assertThat(category.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(category.getColor()).isEqualTo("#FF0000");

        // Test updating only type
        UpdateCategoryRequest typeOnlyRequest = UpdateCategoryRequest.builder()
                .type(CategoryType.INCOME)
                .build();
        mapper.updateEntity(category, typeOnlyRequest);
        assertThat(category.getName()).isEqualTo("Name Only Update");
        assertThat(category.getType()).isEqualTo(CategoryType.INCOME);
        assertThat(category.getColor()).isEqualTo("#FF0000");

        // Test updating only color
        UpdateCategoryRequest colorOnlyRequest = UpdateCategoryRequest.builder()
                .color("#00FF00")
                .build();
        mapper.updateEntity(category, colorOnlyRequest);
        assertThat(category.getName()).isEqualTo("Name Only Update");
        assertThat(category.getType()).isEqualTo(CategoryType.INCOME);
        assertThat(category.getColor()).isEqualTo("#00FF00");
    }
}

