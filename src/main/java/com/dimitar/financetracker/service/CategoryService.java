package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.category.CreateCategoryRequest;
import com.dimitar.financetracker.dto.request.category.MergeCategoriesRequest;
import com.dimitar.financetracker.dto.request.category.UpdateCategoryRequest;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.dto.response.category.ImportCategoriesResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.service.command.category.CreateCategoryCommand;
import com.dimitar.financetracker.service.command.category.DeleteCategoryCommand;
import com.dimitar.financetracker.service.command.category.ImportDefaultCategoriesCommand;
import com.dimitar.financetracker.service.command.category.MergeCategoriesCommand;
import com.dimitar.financetracker.service.command.category.UpdateCategoryCommand;
import com.dimitar.financetracker.service.query.category.GetAllCategoriesQuery;
import com.dimitar.financetracker.service.query.category.GetCategoryByIdQuery;
import com.dimitar.financetracker.service.query.category.GetCategoryByTypeQuery;
import com.dimitar.financetracker.service.query.category.SearchCategoryByNameQuery;
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
    private final MergeCategoriesCommand mergeCategoriesCommand;
    private final ImportDefaultCategoriesCommand importDefaultCategoriesCommand;
    private final SearchCategoryByNameQuery searchCategoryByNameQuery;

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

    public void mergeCategories(MergeCategoriesRequest request) {
        mergeCategoriesCommand.execute(request);
    }

    public ImportCategoriesResponse importDefaultCategories() {
        List<Category> importedCategories = importDefaultCategoriesCommand.execute(null);

        List<CategoryResponse> categoryResponses = importedCategories.stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .type(category.getType())
                        .color(category.getColor())
                        .createdAt(category.getCreatedAt())
                        .updatedAt(category.getUpdatedAt())
                        .build())
                .toList();

        long expenseCount = importedCategories.stream()
                .filter(category -> category.getType() == CategoryType.EXPENSE)
                .count();
        long incomeCount = importedCategories.stream()
                .filter(category -> category.getType() == CategoryType.INCOME)
                .count();

        return ImportCategoriesResponse.builder()
                .totalImported(importedCategories.size())
                .expenseCategories((int) expenseCount)
                .incomeCategories((int) incomeCount)
                .categories(categoryResponses)
                .message("Successfully imported " + importedCategories.size() + " default categories")
                .build();
    }

    public List<CategoryResponse> searchCategoriesByName(String name) {
        return searchCategoryByNameQuery.execute(name);
    }
}
