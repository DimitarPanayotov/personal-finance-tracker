package com.dimitar.financetracker.dto.request.category;

import com.dimitar.financetracker.model.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_COLOR_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_COLOR_TOO_LONG;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_NAME_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_NAME_TOO_LONG;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_TYPE_REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryRequest {
    @NotBlank(message = CATEGORY_NAME_REQUIRED)
    @Size(max = 100, message = CATEGORY_NAME_TOO_LONG)
    private String name;

    @NotNull(message = CATEGORY_TYPE_REQUIRED)
    private CategoryType type;
    @NotBlank(message = CATEGORY_COLOR_REQUIRED)
    @Size(max = 7, message = CATEGORY_COLOR_TOO_LONG)
    private String color;
}
