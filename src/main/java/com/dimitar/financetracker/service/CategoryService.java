package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.category.CreateCategoryRequest;
import com.dimitar.financetracker.dto.request.category.UpdateCategoryRequest;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.service.command.category.CreateCategoryCommand;
import com.dimitar.financetracker.service.command.category.DeleteCategoryCommand;
import com.dimitar.financetracker.service.command.category.UpdateCategoryCommand;
import com.dimitar.financetracker.service.query.category.GetAllCategoriesQuery;
import com.dimitar.financetracker.service.query.category.GetCategoryByIdQuery;
import com.dimitar.financetracker.service.query.category.GetCategoryByTypeQuery;
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
    private final GetCategoryByTypeQuery getCategoryByTypeQuery;

    public CategoryResponse createCategory(CreateCategoryRequest request) {
        return createCategoryCommand.execute(request);
    }

    public CategoryResponse getCategoryById(Long categoryId) {
        return getCategoryByIdQuery.execute(categoryId);
    }

    public List<CategoryResponse> getAllCategories() {
        return getAllCategoriesQuery.execute(null);
    }

    public CategoryResponse updateCategory(UpdateCategoryRequest request) {
        return updateCategoryCommand.execute(request);
    }

    public void deleteCategory(Long categoryId) {
        deleteCategoryCommand.execute(categoryId);
    }

    public List<CategoryResponse> getCategoriesByType(CategoryType type) {
        return getCategoryByTypeQuery.execute(type);
    }
}
