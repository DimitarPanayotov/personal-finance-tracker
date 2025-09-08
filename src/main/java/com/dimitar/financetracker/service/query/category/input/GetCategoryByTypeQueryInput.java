package com.dimitar.financetracker.service.query.category.input;

import com.dimitar.financetracker.model.CategoryType;

public record GetCategoryByTypeQueryInput(CategoryType type, Long userId) {
}
