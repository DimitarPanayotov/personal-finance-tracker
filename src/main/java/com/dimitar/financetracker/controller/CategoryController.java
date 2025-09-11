package com.dimitar.financetracker.controller;

import com.dimitar.financetracker.dto.request.category.CreateCategoryRequest;
import com.dimitar.financetracker.dto.request.category.MergeCategoriesRequest;
import com.dimitar.financetracker.dto.request.category.UpdateCategoryRequest;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.dto.response.category.ImportCategoriesResponse;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
        @Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
        CategoryResponse response = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long categoryId,
                                                           @Valid @RequestBody UpdateCategoryRequest request) {
        request.setCategoryId(categoryId);
        CategoryResponse response = categoryService.updateCategory(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CategoryResponse>> getAllCategoriesByType(@PathVariable CategoryType type) {
        List<CategoryResponse> categories = categoryService.getCategoriesByType(type);
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/merge")
    public ResponseEntity<Void> mergeCategories(@Valid @RequestBody MergeCategoriesRequest request) {
        categoryService.mergeCategories(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/import-defaults")
    public ResponseEntity<ImportCategoriesResponse> importDefaultCategories() {
        ImportCategoriesResponse response = categoryService.importDefaultCategories();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
