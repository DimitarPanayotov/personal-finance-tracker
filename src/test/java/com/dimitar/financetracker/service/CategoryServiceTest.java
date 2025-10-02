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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CreateCategoryCommand createCategoryCommand;
    @Mock private GetCategoryByIdQuery getCategoryByIdQuery;
    @Mock private GetAllCategoriesQuery getAllCategoriesQuery;
    @Mock private UpdateCategoryCommand updateCategoryCommand;
    @Mock private DeleteCategoryCommand deleteCategoryCommand;
    @Mock private GetCategoryByTypeQuery getCategoryByTypeQuery;
    @Mock private MergeCategoriesCommand mergeCategoriesCommand;
    @Mock private ImportDefaultCategoriesCommand importDefaultCategoriesCommand;
    @Mock private SearchCategoryByNameQuery searchCategoryByNameQuery;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(
                createCategoryCommand,
                getCategoryByIdQuery,
                getAllCategoriesQuery,
                updateCategoryCommand,
                deleteCategoryCommand,
                getCategoryByTypeQuery,
                mergeCategoriesCommand,
                importDefaultCategoriesCommand,
                searchCategoryByNameQuery
        );
    }

    @Test
    void createCategory_delegatesToCommand() {
        // Arrange
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Groceries").type(CategoryType.EXPENSE).color("#00FF00")
                .build();
        CategoryResponse expected = CategoryResponse.builder()
                .id(10L).name("Groceries").type(CategoryType.EXPENSE).color("#00FF00")
                .build();
        when(createCategoryCommand.execute(request)).thenReturn(expected);

        // Act
        CategoryResponse actual = categoryService.createCategory(request);

        // Assert
        assertEquals(expected, actual);
        verify(createCategoryCommand).execute(request);
        verifyNoMoreInteractions(createCategoryCommand);
        verifyNoInteractions(getCategoryByIdQuery, getAllCategoriesQuery, updateCategoryCommand, deleteCategoryCommand,
                getCategoryByTypeQuery, mergeCategoriesCommand, importDefaultCategoriesCommand, searchCategoryByNameQuery);
    }

    @Test
    void getCategoryById_delegatesToQuery() {
        // Arrange
        Long id = 5L;
        CategoryResponse expected = CategoryResponse.builder().id(id).name("Salary").type(CategoryType.INCOME).color("#123456").build();
        when(getCategoryByIdQuery.execute(id)).thenReturn(expected);

        // Act
        CategoryResponse actual = categoryService.getCategoryById(id);

        // Assert
        assertEquals(expected, actual);
        verify(getCategoryByIdQuery).execute(id);
        verifyNoMoreInteractions(getCategoryByIdQuery);
        verifyNoInteractions(createCategoryCommand, getAllCategoriesQuery, updateCategoryCommand, deleteCategoryCommand,
                getCategoryByTypeQuery, mergeCategoriesCommand, importDefaultCategoriesCommand, searchCategoryByNameQuery);
    }

    @Test
    void getAllCategories_delegatesToQuery() {
        // Arrange
        List<CategoryResponse> expected = List.of(
                CategoryResponse.builder().id(1L).name("Food").type(CategoryType.EXPENSE).color("#FF0000").build(),
                CategoryResponse.builder().id(2L).name("Salary").type(CategoryType.INCOME).color("#00FF00").build()
        );
        when(getAllCategoriesQuery.execute(null)).thenReturn(expected);

        // Act
        List<CategoryResponse> actual = categoryService.getAllCategories();

        // Assert
        assertEquals(expected, actual);
        verify(getAllCategoriesQuery).execute(null);
        verifyNoMoreInteractions(getAllCategoriesQuery);
        verifyNoInteractions(createCategoryCommand, getCategoryByIdQuery, updateCategoryCommand, deleteCategoryCommand,
                getCategoryByTypeQuery, mergeCategoriesCommand, importDefaultCategoriesCommand, searchCategoryByNameQuery);
    }

    @Test
    void updateCategory_delegatesToCommand() {
        // Arrange
        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .categoryId(1L).name("Dining").color("#AABBCC").type(CategoryType.EXPENSE).build();
        CategoryResponse expected = CategoryResponse.builder()
                .id(1L).name("Dining").color("#AABBCC").type(CategoryType.EXPENSE).build();
        when(updateCategoryCommand.execute(request)).thenReturn(expected);

        // Act
        CategoryResponse actual = categoryService.updateCategory(request);

        // Assert
        assertEquals(expected, actual);
        verify(updateCategoryCommand).execute(request);
        verifyNoMoreInteractions(updateCategoryCommand);
        verifyNoInteractions(createCategoryCommand, getCategoryByIdQuery, getAllCategoriesQuery, deleteCategoryCommand,
                getCategoryByTypeQuery, mergeCategoriesCommand, importDefaultCategoriesCommand, searchCategoryByNameQuery);
    }

    @Test
    void deleteCategory_delegatesToCommand() {
        // Arrange
        Long id = 3L;

        // Act
        categoryService.deleteCategory(id);

        // Assert
        verify(deleteCategoryCommand).execute(id);
        verifyNoMoreInteractions(deleteCategoryCommand);
        verifyNoInteractions(createCategoryCommand, getCategoryByIdQuery, getAllCategoriesQuery, updateCategoryCommand,
                getCategoryByTypeQuery, mergeCategoriesCommand, importDefaultCategoriesCommand, searchCategoryByNameQuery);
    }

    @Test
    void getCategoriesByType_delegatesToQuery() {
        // Arrange
        CategoryType type = CategoryType.EXPENSE;
        List<CategoryResponse> expected = List.of(
                CategoryResponse.builder().id(1L).name("Food").type(CategoryType.EXPENSE).color("#FF0000").build()
        );
        when(getCategoryByTypeQuery.execute(type)).thenReturn(expected);

        // Act
        List<CategoryResponse> actual = categoryService.getCategoriesByType(type);

        // Assert
        assertEquals(expected, actual);
        verify(getCategoryByTypeQuery).execute(type);
        verifyNoMoreInteractions(getCategoryByTypeQuery);
        verifyNoInteractions(createCategoryCommand, getCategoryByIdQuery, getAllCategoriesQuery, updateCategoryCommand,
                deleteCategoryCommand, mergeCategoriesCommand, importDefaultCategoriesCommand, searchCategoryByNameQuery);
    }

    @Test
    void mergeCategories_delegatesToCommand() {
        // Arrange
        MergeCategoriesRequest request = MergeCategoriesRequest.builder()
                .targetCategoryId(2L).sourceCategoryIds(java.util.List.of(1L)).build();

        // Act
        categoryService.mergeCategories(request);

        // Assert
        verify(mergeCategoriesCommand).execute(request);
        verifyNoMoreInteractions(mergeCategoriesCommand);
        verifyNoInteractions(createCategoryCommand, getCategoryByIdQuery, getAllCategoriesQuery, updateCategoryCommand,
                deleteCategoryCommand, getCategoryByTypeQuery, importDefaultCategoriesCommand, searchCategoryByNameQuery);
    }

    @Test
    void importDefaultCategories_buildsAggregatedResponse() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Category c1 = Category.builder().id(1L).name("Food").type(CategoryType.EXPENSE).color("#FF0000").createdAt(now).updatedAt(now).build();
        Category c2 = Category.builder().id(2L).name("Rent").type(CategoryType.EXPENSE).color("#AA0000").createdAt(now).updatedAt(now).build();
        Category c3 = Category.builder().id(3L).name("Salary").type(CategoryType.INCOME).color("#00FF00").createdAt(now).updatedAt(now).build();
        List<Category> imported = List.of(c1, c2, c3);
        when(importDefaultCategoriesCommand.execute(null)).thenReturn(imported);

        // Expected mapped responses and counts
        List<CategoryResponse> expectedResponses = List.of(
                CategoryResponse.builder().id(1L).name("Food").type(CategoryType.EXPENSE).color("#FF0000").createdAt(now).updatedAt(now).build(),
                CategoryResponse.builder().id(2L).name("Rent").type(CategoryType.EXPENSE).color("#AA0000").createdAt(now).updatedAt(now).build(),
                CategoryResponse.builder().id(3L).name("Salary").type(CategoryType.INCOME).color("#00FF00").createdAt(now).updatedAt(now).build()
        );
        ImportCategoriesResponse expected = ImportCategoriesResponse.builder()
                .totalImported(3)
                .expenseCategories(2)
                .incomeCategories(1)
                .categories(expectedResponses)
                .message("Successfully imported 3 default categories")
                .build();

        // Act
        ImportCategoriesResponse actual = categoryService.importDefaultCategories();

        // Assert
        assertEquals(expected, actual);
        verify(importDefaultCategoriesCommand).execute(null);
        verifyNoMoreInteractions(importDefaultCategoriesCommand);
        verifyNoInteractions(createCategoryCommand, getCategoryByIdQuery, getAllCategoriesQuery, updateCategoryCommand,
                deleteCategoryCommand, getCategoryByTypeQuery, mergeCategoriesCommand, searchCategoryByNameQuery);
    }

    @Test
    void importDefaultCategories_returnsEmptyAggregationWhenNoCategories() {
        // Arrange
        when(importDefaultCategoriesCommand.execute(null)).thenReturn(java.util.Collections.emptyList());

        ImportCategoriesResponse expected = ImportCategoriesResponse.builder()
                .totalImported(0)
                .expenseCategories(0)
                .incomeCategories(0)
                .categories(java.util.Collections.emptyList())
                .message("Successfully imported 0 default categories")
                .build();

        // Act
        ImportCategoriesResponse actual = categoryService.importDefaultCategories();

        // Assert
        assertEquals(expected, actual);
        verify(importDefaultCategoriesCommand).execute(null);
        verifyNoMoreInteractions(importDefaultCategoriesCommand);
        verifyNoInteractions(createCategoryCommand, getCategoryByIdQuery, getAllCategoriesQuery, updateCategoryCommand,
                deleteCategoryCommand, getCategoryByTypeQuery, mergeCategoriesCommand, searchCategoryByNameQuery);
    }

    @Test
    void searchCategoriesByName_delegatesToQuery() {
        // Arrange
        String name = "Foo";
        List<CategoryResponse> expected = List.of(
                CategoryResponse.builder().id(1L).name("Food").type(CategoryType.EXPENSE).color("#FF0000").build()
        );
        when(searchCategoryByNameQuery.execute(name)).thenReturn(expected);

        // Act
        List<CategoryResponse> actual = categoryService.searchCategoriesByName(name);

        // Assert
        assertEquals(expected, actual);
        verify(searchCategoryByNameQuery).execute(name);
        verifyNoMoreInteractions(searchCategoryByNameQuery);
        verifyNoInteractions(createCategoryCommand, getCategoryByIdQuery, getAllCategoriesQuery, updateCategoryCommand,
                deleteCategoryCommand, getCategoryByTypeQuery, mergeCategoriesCommand, importDefaultCategoriesCommand);
    }
}
