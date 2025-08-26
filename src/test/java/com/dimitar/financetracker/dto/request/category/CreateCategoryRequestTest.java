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

class CreateCategoryRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequest_shouldPassValidation() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Food & Dining")
                .type(CategoryType.EXPENSE)
                .color("#FF5733")
                .build();

        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void builder_shouldCreateRequestWithAllFields() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Entertainment")
                .type(CategoryType.INCOME)
                .color("#00FF00")
                .build();

        assertThat(request.getName()).isEqualTo("Entertainment");
        assertThat(request.getType()).isEqualTo(CategoryType.INCOME);
        assertThat(request.getColor()).isEqualTo("#00FF00");
    }

    @Test
    void blankName_shouldFailValidation() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("")
                .type(CategoryType.EXPENSE)
                .color("#FF5733")
                .build();

        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void nullName_shouldFailValidation() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name(null)
                .type(CategoryType.EXPENSE)
                .color("#FF5733")
                .build();

        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void whitespaceOnlyName_shouldFailValidation() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("   ")
                .type(CategoryType.EXPENSE)
                .color("#FF5733")
                .build();

        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void tooLongName_shouldFailValidation() {
        String longName = "a".repeat(101);
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name(longName)
                .type(CategoryType.EXPENSE)
                .color("#FF5733")
                .build();

        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void nullType_shouldFailValidation() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Food")
                .type(null)
                .color("#FF5733")
                .build();

        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("type"));
    }

    @Test
    void blankColor_shouldFailValidation() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("")
                .build();

        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
    }

    @Test
    void nullColor_shouldFailValidation() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color(null)
                .build();

        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
    }

    @Test
    void whitespaceOnlyColor_shouldFailValidation() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("   ")
                .build();

        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
    }

    @Test
    void tooLongColor_shouldFailValidation() {
        String longColor = "a".repeat(8);
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color(longColor)
                .build();

        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
    }

    @Test
    void validCategoryTypes_shouldPassValidation() {
        CategoryType[] types = {CategoryType.INCOME, CategoryType.EXPENSE};

        for (CategoryType type : types) {
            CreateCategoryRequest request = CreateCategoryRequest.builder()
                    .name("Test Category")
                    .type(type)
                    .color("#FF0000")
                    .build();

            Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);
            assertThat(violations)
                    .filteredOn(v -> v.getPropertyPath().toString().equals("type"))
                    .isEmpty();
        }
    }

    @Test
    void validColorFormats_shouldPassValidation() {
        String[] validColors = {"#FF0000", "#00FF00", "#0000FF", "#FFFFFF", "#000000"};

        for (String color : validColors) {
            CreateCategoryRequest request = CreateCategoryRequest.builder()
                    .name("Test Category")
                    .type(CategoryType.EXPENSE)
                    .color(color)
                    .build();

            Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);
            assertThat(violations)
                    .filteredOn(v -> v.getPropertyPath().toString().equals("color"))
                    .isEmpty();
        }
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyRequest() {
        CreateCategoryRequest request = new CreateCategoryRequest();

        assertThat(request.getName()).isNull();
        assertThat(request.getType()).isNull();
        assertThat(request.getColor()).isNull();
    }

    @Test
    void allArgsConstructor_shouldCreateRequestWithAllFields() {
        CreateCategoryRequest request = new CreateCategoryRequest(
                "Transportation", CategoryType.EXPENSE, "#0000FF");

        assertThat(request.getName()).isEqualTo("Transportation");
        assertThat(request.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(request.getColor()).isEqualTo("#0000FF");
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        CreateCategoryRequest request = new CreateCategoryRequest();

        request.setName("Utilities");
        request.setType(CategoryType.EXPENSE);
        request.setColor("#FF00FF");

        assertThat(request.getName()).isEqualTo("Utilities");
        assertThat(request.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(request.getColor()).isEqualTo("#FF00FF");
    }

    @Test
    void equals_shouldReturnTrueForSameContent() {
        CreateCategoryRequest request1 = CreateCategoryRequest.builder()
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        CreateCategoryRequest request2 = CreateCategoryRequest.builder()
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentContent() {
        CreateCategoryRequest request1 = CreateCategoryRequest.builder()
                .name("Food")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        CreateCategoryRequest request2 = CreateCategoryRequest.builder()
                .name("Transport")
                .type(CategoryType.EXPENSE)
                .color("#00FF00")
                .build();

        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    void toString_shouldContainAllFields() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
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

