package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.category.CreateCategoryRequest;
import com.dimitar.financetracker.dto.request.category.UpdateCategoryRequest;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.service.command.category.CreateCategoryCommand;
import com.dimitar.financetracker.service.command.category.input.CreateCategoryCommandInput;
import com.dimitar.financetracker.service.command.category.DeleteCategoryCommand;
import com.dimitar.financetracker.service.command.category.input.DeleteCategoryCommandInput;
import com.dimitar.financetracker.service.command.category.UpdateCategoryCommand;
import com.dimitar.financetracker.service.command.category.input.UpdateCategoryCommandInput;
import com.dimitar.financetracker.service.query.category.GetAllCategoriesQuery;
import com.dimitar.financetracker.service.query.category.GetCategoryByIdQuery;
import com.dimitar.financetracker.service.query.category.input.GetCategoryByIdQueryInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CreateCategoryCommand createCategoryCommand;
    private final GetCategoryByIdQuery getCategoryByIdQuery;
    private final GetAllCategoriesQuery getAllCategoriesQuery;
    private final UpdateCategoryCommand updateCategoryCommand;
    private final DeleteCategoryCommand deleteCategoryCommand;

    public CategoryResponse createCategory(CreateCategoryRequest request, Long userId) {
        CreateCategoryCommandInput commandRequest = new CreateCategoryCommandInput(request, userId);
        return createCategoryCommand.execute(commandRequest);
    }

    public CategoryResponse getCategoryById(Long categoryId, Long userId) {
        GetCategoryByIdQueryInput request = new GetCategoryByIdQueryInput(categoryId, userId);
        return getCategoryByIdQuery.execute(request);
    }

    public List<CategoryResponse> getAllCategories(Long userId) {
        return getAllCategoriesQuery.execute(userId);
    }

    public CategoryResponse updateCategory(UpdateCategoryRequest request, Long userId, Long categoryId) {
        UpdateCategoryCommandInput updateRequest = new UpdateCategoryCommandInput(request, userId, categoryId);
        return updateCategoryCommand.execute(updateRequest);
    }

    public void deleteCategory(Long categoryId, Long userId) {
        DeleteCategoryCommandInput deleteRequest = new DeleteCategoryCommandInput(categoryId, userId);
        deleteCategoryCommand.execute(deleteRequest);
    }
}
