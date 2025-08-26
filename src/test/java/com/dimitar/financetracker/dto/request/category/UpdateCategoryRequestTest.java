package com.dimitar.financetracker.dto.request.category;

import com.dimitar.financetracker.model.CategoryType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateCategoryRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequest_shouldPassValidation() {
        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name("Updated Food & Dining")
                .type(CategoryType.INCOME)
                .color("#FF5733")
                .build();

        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void emptyRequest_shouldPassValidation() {
        UpdateCategoryRequest request = UpdateCategoryRequest.builder().build();

        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void nullFields_shouldPassValidation() {
        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name(null)
                .type(null)
                .color(null)
                .build();

        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void emptyStrings_shouldPassValidation() {
        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name("")
                .color("")
                .build();

        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void tooLongName_shouldFailValidation() {
        String longName = "a".repeat(101);
        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name(longName)
                .type(CategoryType.EXPENSE)
                .color("#FF5733")
                .build();

        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void tooLongColor_shouldFailValidation() {
        String longColor = "a".repeat(8); // assuming COLOR_LENGTH is 7
        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color(longColor)
                .build();

        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
    }

    @Test
    void validPartialUpdate_shouldPassValidation() {
        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name("New Name")
                // type and color are null
                .build();

        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void validCategoryTypes_shouldPassValidation() {
        CategoryType[] types = {CategoryType.INCOME, CategoryType.EXPENSE};

        for (CategoryType type : types) {
            UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                    .name("Test Category")
                    .type(type)
                    .color("#FF0000")
                    .build();

            Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
            assertThat(violations)
                    .filteredOn(v -> v.getPropertyPath().toString().equals("type"))
                    .isEmpty();
        }
    }

    @Test
    void validColorFormats_shouldPassValidation() {
        String[] validColors = {"#FF0000", "#00FF00", "#0000FF", "#FFFFFF", "#000000"};

        for (String color : validColors) {
            UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                    .name("Test Category")
                    .type(CategoryType.EXPENSE)
                    .color(color)
                    .build();

            Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
            assertThat(violations)
                    .filteredOn(v -> v.getPropertyPath().toString().equals("color"))
                    .isEmpty();
        }
    }

    @Test
    void whitespaceOnlyFields_shouldPassValidation() {
        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name("   ")
                .color("   ")
                .build();

        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyRequest() {
        UpdateCategoryRequest request = new UpdateCategoryRequest();

        assertThat(request.getName()).isNull();
        assertThat(request.getType()).isNull();
        assertThat(request.getColor()).isNull();
    }

    @Test
    void allArgsConstructor_shouldCreateRequestWithAllFields() {
        UpdateCategoryRequest request = new UpdateCategoryRequest(
                "Transportation", CategoryType.EXPENSE, "#0000FF");

        assertThat(request.getName()).isEqualTo("Transportation");
        assertThat(request.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(request.getColor()).isEqualTo("#0000FF");
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        UpdateCategoryRequest request = new UpdateCategoryRequest();

        request.setName("Utilities");
        request.setType(CategoryType.EXPENSE);
        request.setColor("#FF00FF");

        assertThat(request.getName()).isEqualTo("Utilities");
        assertThat(request.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(request.getColor()).isEqualTo("#FF00FF");
    }

    @Test
    void equals_shouldReturnTrueForSameContent() {
        UpdateCategoryRequest request1 = UpdateCategoryRequest.builder()
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        UpdateCategoryRequest request2 = UpdateCategoryRequest.builder()
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentContent() {
        UpdateCategoryRequest request1 = UpdateCategoryRequest.builder()
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        UpdateCategoryRequest request2 = UpdateCategoryRequest.builder()
                .name("Transport")
                .type(CategoryType.EXPENSE)
                .color("#00FF00")
                .build();

        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    void toString_shouldContainAllFields() {
        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name("Entertainment")
                .type(CategoryType.EXPENSE)
                .color("#0000FF")
                .build();

        String toString = request.toString();

        assertThat(toString).contains("name=Entertainment");
        assertThat(toString).contains("type=EXPENSE");
        assertThat(toString).contains("color=#0000FF");
    }
}

