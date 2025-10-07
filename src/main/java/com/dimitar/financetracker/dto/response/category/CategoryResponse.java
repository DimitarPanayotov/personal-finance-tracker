package com.dimitar.financetracker.dto.response.category;

import com.dimitar.financetracker.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Category details returned to clients.")
public class CategoryResponse {
    @Schema(description = "Category identifier", example = "101", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(description = "Category name", example = "Groceries", accessMode = Schema.AccessMode.READ_ONLY)
    private String name;
    @Schema(description = "Category type (INCOME or EXPENSE)", example = "EXPENSE", accessMode = Schema.AccessMode.READ_ONLY)
    private CategoryType type;
    @Schema(description = "Hex/theme color for UI", example = "#FFAA00", accessMode = Schema.AccessMode.READ_ONLY)
    private String color;
    @Schema(description = "Creation timestamp (UTC)", example = "2025-10-01T12:34:56", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    @Schema(description = "Last update timestamp (UTC)", example = "2025-10-07T09:20:15", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
