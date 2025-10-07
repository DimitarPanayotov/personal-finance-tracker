package com.dimitar.financetracker.controller;

import com.dimitar.financetracker.dto.request.category.CreateCategoryRequest;
import com.dimitar.financetracker.dto.request.category.MergeCategoriesRequest;
import com.dimitar.financetracker.dto.request.category.UpdateCategoryRequest;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.dto.response.category.ImportCategoriesResponse;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Operations for managing user-defined income and expense categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(
            summary = "Create a new category",
            description = "Creates a new category for the authenticated user. Category names are typically unique per user per type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed for supplied category data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "409", description = "Conflict - category with same name/type already exists")
    })
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
        @Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Import default categories",
            description = "Imports a predefined set of default categories (if not already present) for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Default categories imported"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "409", description = "Conflict - defaults already imported or partially exist")
    })
    @PostMapping("/import-defaults")
    public ResponseEntity<ImportCategoriesResponse> importDefaultCategories() {
        ImportCategoriesResponse response = categoryService.importDefaultCategories();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Merge categories",
            description = "Merges source categories into a target category (e.g., reassigns transactions) and removes or deactivates the source."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories successfully merged"),
            @ApiResponse(responseCode = "400", description = "Invalid merge request (e.g., same IDs)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "One or both categories not found")
    })
    @PostMapping("/merge")
    public ResponseEntity<Void> mergeCategories(@Valid @RequestBody MergeCategoriesRequest request) {
        categoryService.mergeCategories(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Update a category",
            description = "Updates mutable attributes (e.g., name, type) of an existing category owned by the user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed for supplied category data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - category with updated name/type already exists")
    })
    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long categoryId,
                                                           @Valid @RequestBody UpdateCategoryRequest request) {
        request.setCategoryId(categoryId);
        CategoryResponse response = categoryService.updateCategory(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a category",
            description = "Deletes (or possibly soft-deletes) a category by its ID. Associated transactions may need reassignment depending on business rules."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - category cannot be deleted due to existing dependencies")
    })
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "List all categories",
            description = "Retrieves all categories owned by the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(
            summary = "Get category by ID",
            description = "Retrieves a single category by its ID if it belongs to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
        CategoryResponse response = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List categories by type",
            description = "Retrieves all categories of the specified type (INCOME or EXPENSE) owned by the user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid category type supplied"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping("/type/{type}")
    public ResponseEntity<List<CategoryResponse>> getAllCategoriesByType(@PathVariable CategoryType type) {
        List<CategoryResponse> categories = categoryService.getCategoriesByType(type);
        return ResponseEntity.ok(categories);
    }

    @Operation(
            summary = "Search categories by name",
            description = "Performs a case-insensitive search for categories whose names contain the query substring."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid search query parameter 'q'"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponse>> searchCategoriesByName(@RequestParam("q") String q) {
        List<CategoryResponse> categories = categoryService.searchCategoriesByName(q);
        return ResponseEntity.ok(categories);
    }

}
