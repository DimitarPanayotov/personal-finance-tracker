package com.dimitar.financetracker.dto.response.category;

import com.dimitar.financetracker.model.CategoryType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategorySummaryResponseTest {

    @Test
    void builder_shouldCreateValidCategorySummaryResponse() {
        CategorySummaryResponse response = CategorySummaryResponse.builder()
                .id(1L)
                .name("Food & Dining")
                .type(CategoryType.EXPENSE)
                .color("#FF5733")
                .build();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Food & Dining");
        assertThat(response.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(response.getColor()).isEqualTo("#FF5733");
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyResponse() {
        CategorySummaryResponse response = new CategorySummaryResponse();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getName()).isNull();
        assertThat(response.getType()).isNull();
        assertThat(response.getColor()).isNull();
    }

    @Test
    void allArgsConstructor_shouldCreateResponseWithAllFields() {
        CategorySummaryResponse response = new CategorySummaryResponse(
                2L,
                "Transportation",
                CategoryType.EXPENSE,
                "#0000FF"
        );

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getName()).isEqualTo("Transportation");
        assertThat(response.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(response.getColor()).isEqualTo("#0000FF");
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        CategorySummaryResponse response = new CategorySummaryResponse();

        response.setId(3L);
        response.setName("Entertainment");
        response.setType(CategoryType.EXPENSE);
        response.setColor("#00FF00");

        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.getName()).isEqualTo("Entertainment");
        assertThat(response.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(response.getColor()).isEqualTo("#00FF00");
    }

    @Test
    void equals_shouldReturnTrueForSameContent() {
        CategorySummaryResponse response1 = CategorySummaryResponse.builder()
                .id(1L)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        CategorySummaryResponse response2 = CategorySummaryResponse.builder()
                .id(1L)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentContent() {
        CategorySummaryResponse response1 = CategorySummaryResponse.builder()
                .id(1L)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        CategorySummaryResponse response2 = CategorySummaryResponse.builder()
                .id(2L)
                .name("Transport")
                .type(CategoryType.EXPENSE)
                .color("#00FF00")
                .build();

        assertThat(response1).isNotEqualTo(response2);
    }

    @Test
    void toString_shouldContainAllFields() {
        CategorySummaryResponse response = CategorySummaryResponse.builder()
                .id(1L)
                .name("Utilities")
                .type(CategoryType.EXPENSE)
                .color("#FF00FF")
                .build();

        String toString = response.toString();

        assertThat(toString).contains("id=1");
        assertThat(toString).contains("name=Utilities");
        assertThat(toString).contains("type=EXPENSE");
        assertThat(toString).contains("color=#FF00FF");
    }

    @Test
    void withNullValues_shouldHandleCorrectly() {
        CategorySummaryResponse response = CategorySummaryResponse.builder()
                .id(null)
                .name(null)
                .type(null)
                .color(null)
                .build();

        assertThat(response.getId()).isNull();
        assertThat(response.getName()).isNull();
        assertThat(response.getType()).isNull();
        assertThat(response.getColor()).isNull();
    }

    @Test
    void withDifferentCategoryTypes_shouldPreserveType() {
        CategoryType[] types = {CategoryType.INCOME, CategoryType.EXPENSE};

        for (CategoryType type : types) {
            CategorySummaryResponse response = CategorySummaryResponse.builder()
                    .id(1L)
                    .name("Test Category")
                    .type(type)
                    .color("#FF0000")
                    .build();

            assertThat(response.getType()).isEqualTo(type);
        }
    }

    @Test
    void withDifferentColorFormats_shouldPreserveFormat() {
        String[] colors = {
                "#FF0000", "#00FF00", "#0000FF", "#FFFFFF", "#000000"
        };

        for (String color : colors) {
            CategorySummaryResponse response = CategorySummaryResponse.builder()
                    .id(1L)
                    .name("Category")
                    .type(CategoryType.EXPENSE)
                    .color(color)
                    .build();

            assertThat(response.getColor()).isEqualTo(color);
        }
    }
}

