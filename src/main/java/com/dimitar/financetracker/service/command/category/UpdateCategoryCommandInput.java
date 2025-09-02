package com.dimitar.financetracker.service.command.category;

import com.dimitar.financetracker.dto.request.category.UpdateCategoryRequest;

public record UpdateCategoryCommandInput(UpdateCategoryRequest request, Long userId, Long categoryId) {
}
