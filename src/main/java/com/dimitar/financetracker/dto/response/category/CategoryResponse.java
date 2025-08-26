package com.dimitar.financetracker.dto.response.category;

import com.dimitar.financetracker.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private CategoryType type;
    private String color;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
