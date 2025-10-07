package com.dimitar.financetracker.dto.request.category;

import com.dimitar.financetracker.model.CategoryType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.dimitar.financetracker.util.DatabaseConstants.CATEGORY_NAME_MAX_LENGTH;
import static com.dimitar.financetracker.util.DatabaseConstants.COLOR_LENGTH;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_COLOR_TOO_LONG;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_NAME_TOO_LONG;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload to update an existing category. Send only fields you wish to change.")
public class UpdateCategoryRequest {
    @Schema(description = "ID of the category being updated (provided via path, not in body)", example = "200", accessMode = Schema.AccessMode.READ_ONLY)
    private Long categoryId;

    @Size(max = CATEGORY_NAME_MAX_LENGTH, message = CATEGORY_NAME_TOO_LONG)
    @Schema(description = "New category name (optional)", example = "Groceries & Household")
    private String name;

    @Schema(description = "New category type (optional). If omitted, existing type is kept.", example = "EXPENSE")
    private CategoryType type;

    @Size(max = COLOR_LENGTH, message = CATEGORY_COLOR_TOO_LONG)
    @Schema(description = "New hex/theme color (optional)", example = "#4CAF50")
    private String color;
}
