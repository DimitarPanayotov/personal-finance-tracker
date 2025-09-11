package com.dimitar.financetracker.dto.response.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportCategoriesResponse {
    private int totalImported;
    private int expenseCategories;
    private int incomeCategories;
    private List<CategoryResponse> categories;
    private String message;
}
