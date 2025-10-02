package com.dimitar.financetracker.service.query.category;

import com.dimitar.financetracker.dto.mapper.CategoryMapper;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCategoryByTypeQueryTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    private GetCategoryByTypeQuery query;

    @BeforeEach
    void setUp() {
        query = new GetCategoryByTypeQuery(authenticationFacade, categoryRepository, categoryMapper);
    }

    @Test
    void execute_returnsMappedCategoriesOfTypeForAuthenticatedUser() {
        Long userId = 33L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Category c1 = Category.builder().id(1L).name("Groceries").type(CategoryType.EXPENSE).build();
        Category c2 = Category.builder().id(2L).name("Transport").type(CategoryType.EXPENSE).build();
        when(categoryRepository.findAllByUserIdAndType(userId, CategoryType.EXPENSE)).thenReturn(List.of(c1, c2));

        CategoryResponse r1 = CategoryResponse.builder().id(1L).name("Groceries").type(CategoryType.EXPENSE).build();
        CategoryResponse r2 = CategoryResponse.builder().id(2L).name("Transport").type(CategoryType.EXPENSE).build();
        when(categoryMapper.toResponse(c1)).thenReturn(r1);
        when(categoryMapper.toResponse(c2)).thenReturn(r2);

        List<CategoryResponse> result = query.execute(CategoryType.EXPENSE);

        assertEquals(List.of(r1, r2), result);
        verify(categoryRepository).findAllByUserIdAndType(userId, CategoryType.EXPENSE);
        verify(categoryMapper).toResponse(c1);
        verify(categoryMapper).toResponse(c2);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void execute_returnsEmptyList_whenNoCategoriesOfType() {
        Long userId = 33L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(categoryRepository.findAllByUserIdAndType(userId, CategoryType.INCOME)).thenReturn(List.of());

        List<CategoryResponse> result = query.execute(CategoryType.INCOME);

        assertEquals(List.of(), result);
        verify(categoryRepository).findAllByUserIdAndType(userId, CategoryType.INCOME);
        verifyNoInteractions(categoryMapper);
    }
}

