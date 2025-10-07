package com.dimitar.financetracker.dto.request.category;

import com.dimitar.financetracker.model.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.dimitar.financetracker.util.DatabaseConstants.CATEGORY_NAME_MAX_LENGTH;
import static com.dimitar.financetracker.util.DatabaseConstants.COLOR_LENGTH;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_COLOR_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_COLOR_TOO_LONG;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_NAME_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_NAME_TOO_LONG;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_TYPE_REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload to create a new category for the authenticated user.")
public class CreateCategoryRequest {
    @NotBlank(message = CATEGORY_NAME_REQUIRED)
    @Size(max = CATEGORY_NAME_MAX_LENGTH, message = CATEGORY_NAME_TOO_LONG)
    @Schema(description = "Category name (unique per user within its type)", example = "Groceries")
    private String name;

    @NotNull(message = CATEGORY_TYPE_REQUIRED)
    @Schema(description = "Category type (e.g., INCOME or EXPENSE)", example = "EXPENSE")
    private CategoryType type;

    @NotBlank(message = CATEGORY_COLOR_REQUIRED)
    @Size(max = COLOR_LENGTH, message = CATEGORY_COLOR_TOO_LONG)
    @Schema(description = "Hex or theme color identifier used for UI display", example = "#FFAA00")
    private String color;
}
