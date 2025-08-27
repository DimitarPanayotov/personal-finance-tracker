package com.dimitar.financetracker.dto.request.category;

import com.dimitar.financetracker.model.CategoryType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_COLOR_TOO_LONG;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_NAME_TOO_LONG;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCategoryRequest {
    @Size(max = 100, message = CATEGORY_NAME_TOO_LONG)
    private String name;

    private CategoryType type;

    @Size(max = 7, message = CATEGORY_COLOR_TOO_LONG)
    private String color;
}
