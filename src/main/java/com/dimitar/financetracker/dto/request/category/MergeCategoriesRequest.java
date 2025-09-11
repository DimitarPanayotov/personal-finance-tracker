package com.dimitar.financetracker.dto.request.category;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MergeCategoriesRequest {

    @NotNull(message = CATEGORY_REQUIRED)
    private Long targetCategoryId;

    @NotEmpty(message = "Source categories cannot be empty")
    @Size(min = 1, message = "At least one source category is required")
    private List<Long> sourceCategoryIds;
}
