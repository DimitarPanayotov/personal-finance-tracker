package com.dimitar.financetracker.dto.response.category;

import com.dimitar.financetracker.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySummaryResponse {
    private Long id;
    private String name;
    private CategoryType type;
    private String color;
}
