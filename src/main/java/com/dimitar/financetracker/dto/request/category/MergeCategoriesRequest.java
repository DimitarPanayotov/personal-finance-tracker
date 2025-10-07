package com.dimitar.financetracker.dto.request.category;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Payload to merge one or more source categories into a target category. After merge, source categories will be retired or removed according to business rules.")
public class MergeCategoriesRequest {

    @NotNull(message = CATEGORY_REQUIRED)
    @Schema(description = "Category ID that will remain after the merge (target)", example = "10")
    private Long targetCategoryId;

    @NotEmpty(message = "Source categories cannot be empty")
    @Size(min = 1, message = "At least one source category is required")
    @Schema(description = "IDs of categories to merge into the target", example = "[21, 22, 23]")
    private List<Long> sourceCategoryIds;
}
