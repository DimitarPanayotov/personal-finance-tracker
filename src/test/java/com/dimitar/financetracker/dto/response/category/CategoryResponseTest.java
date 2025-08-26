package com.dimitar.financetracker.dto.response.category;

import com.dimitar.financetracker.model.CategoryType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryResponseTest {

    @Test
    void builder_shouldCreateValidCategoryResponse() {
        LocalDateTime now = LocalDateTime.now();

        CategoryResponse response = CategoryResponse.builder()
                .id(1L)
                .name("Food & Dining")
                .type(CategoryType.EXPENSE)
                .color("#FF5733")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Food & Dining");
        assertThat(response.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(response.getColor()).isEqualTo("#FF5733");
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyResponse() {
        CategoryResponse response = new CategoryResponse();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getName()).isNull();
        assertThat(response.getType()).isNull();
        assertThat(response.getColor()).isNull();
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
    }

    @Test
    void allArgsConstructor_shouldCreateResponseWithAllFields() {
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 2, 15, 30);

        CategoryResponse response = new CategoryResponse(
                2L,
                "Transportation",
                CategoryType.EXPENSE,
                "#0000FF",
                createdAt,
                updatedAt
        );

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getName()).isEqualTo("Transportation");
        assertThat(response.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(response.getColor()).isEqualTo("#0000FF");
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        CategoryResponse response = new CategoryResponse();
        LocalDateTime now = LocalDateTime.now();

        response.setId(3L);
        response.setName("Entertainment");
        response.setType(CategoryType.EXPENSE);
        response.setColor("#00FF00");
        response.setCreatedAt(now);
        response.setUpdatedAt(now);

        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.getName()).isEqualTo("Entertainment");
        assertThat(response.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(response.getColor()).isEqualTo("#00FF00");
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void equals_shouldReturnTrueForSameContent() {
        LocalDateTime time = LocalDateTime.now();

        CategoryResponse response1 = CategoryResponse.builder()
                .id(1L)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .createdAt(time)
                .updatedAt(time)
                .build();

        CategoryResponse response2 = CategoryResponse.builder()
                .id(1L)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .createdAt(time)
                .updatedAt(time)
                .build();

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentContent() {
        LocalDateTime time = LocalDateTime.now();

        CategoryResponse response1 = CategoryResponse.builder()
                .id(1L)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .createdAt(time)
                .updatedAt(time)
                .build();

        CategoryResponse response2 = CategoryResponse.builder()
                .id(2L)
                .name("Transport")
                .type(CategoryType.EXPENSE)
                .color("#00FF00")
                .createdAt(time)
                .updatedAt(time)
                .build();

        assertThat(response1).isNotEqualTo(response2);
    }

    @Test
    void toString_shouldContainAllFields() {
        LocalDateTime now = LocalDateTime.now();

        CategoryResponse response = CategoryResponse.builder()
                .id(1L)
                .name("Utilities")
                .type(CategoryType.EXPENSE)
                .color("#FF00FF")
                .createdAt(now)
                .updatedAt(now)
                .build();

        String toString = response.toString();

        assertThat(toString).contains("id=1");
        assertThat(toString).contains("name=Utilities");
        assertThat(toString).contains("type=EXPENSE");
        assertThat(toString).contains("color=#FF00FF");
        assertThat(toString).contains("createdAt=" + now);
        assertThat(toString).contains("updatedAt=" + now);
    }

    @Test
    void withNullValues_shouldHandleCorrectly() {
        CategoryResponse response = CategoryResponse.builder()
                .id(null)
                .name(null)
                .type(null)
                .color(null)
                .createdAt(null)
                .updatedAt(null)
                .build();

        assertThat(response.getId()).isNull();
        assertThat(response.getName()).isNull();
        assertThat(response.getType()).isNull();
        assertThat(response.getColor()).isNull();
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
    }

    @Test
    void withDifferentCategoryTypes_shouldPreserveType() {
        CategoryType[] types = {CategoryType.INCOME, CategoryType.EXPENSE};

        for (CategoryType type : types) {
            CategoryResponse response = CategoryResponse.builder()
                    .id(1L)
                    .name("Test Category")
                    .type(type)
                    .color("#FF0000")
                    .build();

            assertThat(response.getType()).isEqualTo(type);
        }
    }
}

