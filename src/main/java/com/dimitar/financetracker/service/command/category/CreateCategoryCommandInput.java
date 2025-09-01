package com.dimitar.financetracker.service.command.category;

import com.dimitar.financetracker.dto.request.category.CreateCategoryRequest;

public record CreateCategoryCommandInput(CreateCategoryRequest request, Long userId) {
}
