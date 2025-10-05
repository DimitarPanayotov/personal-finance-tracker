package com.dimitar.financetracker.controller;

import com.dimitar.financetracker.dto.request.category.CreateCategoryRequest;
import com.dimitar.financetracker.dto.request.category.MergeCategoriesRequest;
import com.dimitar.financetracker.dto.request.category.UpdateCategoryRequest;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.dto.response.category.ImportCategoriesResponse;
import com.dimitar.financetracker.exception.GlobalExceptionHandler;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    CategoryService categoryService;

    @BeforeEach
    void setUp() {
        CategoryController controller = new CategoryController(categoryService);
        this.objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(objectMapper);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(jsonConverter)
                .setValidator(validator)
                .build();
    }

    @Nested
    class CreateCategory {
        @Test
        @DisplayName("POST /api/categories returns 201 with CategoryResponse on success")
        void create_success() throws Exception {
            CategoryResponse response = CategoryResponse.builder()
                    .id(10L)
                    .name("Groceries")
                    .type(CategoryType.EXPENSE)
                    .color("#FF0000")
                    .createdAt(LocalDateTime.now().minusDays(2))
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(categoryService.createCategory(any(CreateCategoryRequest.class)))
                    .thenReturn(response);

            CreateCategoryRequest request = CreateCategoryRequest.builder()
                    .name("Groceries")
                    .type(CategoryType.EXPENSE)
                    .color("#FF0000")
                    .build();

            mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(10)))
                    .andExpect(jsonPath("$.name", is("Groceries")))
                    .andExpect(jsonPath("$.type", is("EXPENSE")))
                    .andExpect(jsonPath("$.color", is("#FF0000")));
        }

        @Test
        @DisplayName("POST /api/categories returns 400 with validation errors when payload invalid")
        void create_validationErrors() throws Exception {
            String payload = """
            {
              "name": "",
              "type": null,
              "color": ""
            }
            """;

            mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")))
                    .andExpect(jsonPath("$.message", is("Validation failed for user registration")))
                    .andExpect(jsonPath("$.path", is("/api/categories")))
                    .andExpect(jsonPath("$.errors.name", is("Category name is required")))
                    .andExpect(jsonPath("$.errors.type", is("Category type is required")))
                    .andExpect(jsonPath("$.errors.color", is("Color is required")));
        }
    }

    @Nested
    class ImportDefaults {
        @Test
        @DisplayName("POST /api/categories/import-defaults returns 201 with ImportCategoriesResponse")
        void importDefaults_success() throws Exception {
            CategoryResponse c1 = CategoryResponse.builder().id(1L).name("Salary").type(CategoryType.INCOME).color("#00FF00").build();
            CategoryResponse c2 = CategoryResponse.builder().id(2L).name("Rent").type(CategoryType.EXPENSE).color("#0000FF").build();
            ImportCategoriesResponse response = ImportCategoriesResponse.builder()
                    .totalImported(2)
                    .incomeCategories(1)
                    .expenseCategories(1)
                    .categories(List.of(c1, c2))
                    .message("Successfully imported 2 default categories")
                    .build();

            when(categoryService.importDefaultCategories()).thenReturn(response);

            mockMvc.perform(post("/api/categories/import-defaults"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.totalImported", is(2)))
                    .andExpect(jsonPath("$.incomeCategories", is(1)))
                    .andExpect(jsonPath("$.expenseCategories", is(1)))
                    .andExpect(jsonPath("$.categories", hasSize(2)))
                    .andExpect(jsonPath("$.message", containsString("Successfully imported")));
        }
    }

    @Nested
    class MergeCategories {
        @Test
        @DisplayName("POST /api/categories/merge returns 200 on success")
        void merge_success() throws Exception {
            doNothing().when(categoryService).mergeCategories(any(MergeCategoriesRequest.class));

            MergeCategoriesRequest request = MergeCategoriesRequest.builder()
                    .targetCategoryId(2L)
                    .sourceCategoryIds(List.of(3L, 4L))
                    .build();

            mockMvc.perform(post("/api/categories/merge")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("POST /api/categories/merge returns 400 with validation errors when payload invalid")
        void merge_validationErrors() throws Exception {
            MergeCategoriesRequest invalid = MergeCategoriesRequest.builder()
                    .targetCategoryId(null)
                    .sourceCategoryIds(List.of())
                    .build();

            mockMvc.perform(post("/api/categories/merge")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")))
                    .andExpect(jsonPath("$.message", is("Validation failed for user registration")))
                    .andExpect(jsonPath("$.path", is("/api/categories/merge")))
                    .andExpect(jsonPath("$.errors.targetCategoryId", is("Category is required")))
                    .andExpect(jsonPath("$.errors.sourceCategoryIds", anyOf(is("Source categories cannot be empty"), is("At least one source category is required"))));
        }

        @Test
        @DisplayName("POST /api/categories/merge returns 404 when a category is not found")
        void merge_notFound() throws Exception {
            doThrow(new CategoryDoesNotExistException("Category not found"))
                    .when(categoryService).mergeCategories(any(MergeCategoriesRequest.class));

            MergeCategoriesRequest request = MergeCategoriesRequest.builder()
                    .targetCategoryId(99L)
                    .sourceCategoryIds(List.of(1L))
                    .build();

            mockMvc.perform(post("/api/categories/merge")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("Category not found")))
                    .andExpect(jsonPath("$.path", is("/api/categories/merge")));
        }
    }

    @Nested
    class UpdateCategory {
        @Test
        @DisplayName("PATCH /api/categories/{id} returns 200 with CategoryResponse on success")
        void update_success() throws Exception {
            CategoryResponse response = CategoryResponse.builder()
                    .id(5L)
                    .name("Essentials")
                    .type(CategoryType.EXPENSE)
                    .color("#FFFFFF")
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(categoryService.updateCategory(any(UpdateCategoryRequest.class))).thenReturn(response);

            UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                    .name("Essentials")
                    .type(CategoryType.EXPENSE)
                    .color("#FFFFFF")
                    .build();

            mockMvc.perform(patch("/api/categories/{id}", 5)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(5)))
                    .andExpect(jsonPath("$.name", is("Essentials")))
                    .andExpect(jsonPath("$.type", is("EXPENSE")))
                    .andExpect(jsonPath("$.color", is("#FFFFFF")));
        }

        @Test
        @DisplayName("PATCH /api/categories/{id} returns 400 with validation errors when payload invalid")
        void update_validationErrors() throws Exception {
            String longName = "x".repeat(101); // > 100
            String longColor = "#1234567"; // 8 chars, > 7
            UpdateCategoryRequest invalid = UpdateCategoryRequest.builder()
                    .name(longName)
                    .color(longColor)
                    .build();

            mockMvc.perform(patch("/api/categories/{id}", 7)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")))
                    .andExpect(jsonPath("$.message", is("Validation failed for user registration")))
                    .andExpect(jsonPath("$.path", is("/api/categories/7")))
                    .andExpect(jsonPath("$.errors.name", is("Category name must be less than 100 characters")))
                    .andExpect(jsonPath("$.errors.color", is("Color must be less than 7 characters")));
        }

        @Test
        @DisplayName("PATCH /api/categories/{id} returns 404 when category not found")
        void update_notFound() throws Exception {
            when(categoryService.updateCategory(any(UpdateCategoryRequest.class)))
                    .thenThrow(new CategoryDoesNotExistException("Category not found"));

            UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                    .name("Other")
                    .build();

            mockMvc.perform(patch("/api/categories/{id}", 123)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("Category not found")))
                    .andExpect(jsonPath("$.path", is("/api/categories/123")));
        }
    }

    @Nested
    class DeleteCategory {
        @Test
        @DisplayName("DELETE /api/categories/{id} returns 204 on success")
        void delete_success() throws Exception {
            doNothing().when(categoryService).deleteCategory(eq(10L));

            mockMvc.perform(delete("/api/categories/{id}", 10))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("DELETE /api/categories/{id} returns 404 when category not found")
        void delete_notFound() throws Exception {
            doThrow(new CategoryDoesNotExistException("Category not found"))
                    .when(categoryService).deleteCategory(eq(99L));

            mockMvc.perform(delete("/api/categories/{id}", 99))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("Category not found")))
                    .andExpect(jsonPath("$.path", is("/api/categories/99")));
        }
    }

    @Nested
    class GetCategories {
        @Test
        @DisplayName("GET /api/categories returns 200 with list of CategoryResponse")
        void getAll_success() throws Exception {
            CategoryResponse c1 = CategoryResponse.builder().id(1L).name("Food").type(CategoryType.EXPENSE).color("#AA0000").build();
            CategoryResponse c2 = CategoryResponse.builder().id(2L).name("Salary").type(CategoryType.INCOME).color("#00AA00").build();
            when(categoryService.getAllCategories()).thenReturn(List.of(c1, c2));

            mockMvc.perform(get("/api/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].name", is("Food")))
                    .andExpect(jsonPath("$[1].type", is("INCOME")));
        }

        @Test
        @DisplayName("GET /api/categories/{id} returns 200 with CategoryResponse")
        void getById_success() throws Exception {
            CategoryResponse c = CategoryResponse.builder().id(3L).name("Bills").type(CategoryType.EXPENSE).color("#123456").build();
            when(categoryService.getCategoryById(3L)).thenReturn(c);

            mockMvc.perform(get("/api/categories/{id}", 3))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(3)))
                    .andExpect(jsonPath("$.name", is("Bills")))
                    .andExpect(jsonPath("$.type", is("EXPENSE")));
        }

        @Test
        @DisplayName("GET /api/categories/{id} returns 404 when category not found")
        void getById_notFound() throws Exception {
            when(categoryService.getCategoryById(77L)).thenThrow(new CategoryDoesNotExistException("Category not found"));

            mockMvc.perform(get("/api/categories/{id}", 77))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not found")))
                    .andExpect(jsonPath("$.message", is("Category not found")))
                    .andExpect(jsonPath("$.path", is("/api/categories/77")));
        }

        @Test
        @DisplayName("GET /api/categories/type/{type} returns 200 with list")
        void getByType_success() throws Exception {
            CategoryResponse c = CategoryResponse.builder().id(4L).name("Transport").type(CategoryType.EXPENSE).color("#BBBBBB").build();
            when(categoryService.getCategoriesByType(CategoryType.EXPENSE)).thenReturn(List.of(c));

            mockMvc.perform(get("/api/categories/type/{type}", "EXPENSE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].type", is("EXPENSE")));
        }

        @Test
        @DisplayName("GET /api/categories/search?q={query} returns 200 with list")
        void searchByName_success() throws Exception {
            CategoryResponse c = CategoryResponse.builder().id(5L).name("Food & Drinks").type(CategoryType.EXPENSE).color("#CCCCCC").build();
            when(categoryService.searchCategoriesByName("food")).thenReturn(List.of(c));

            mockMvc.perform(get("/api/categories/search").param("q", "food"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name", containsString("Food")));
        }
    }
}
