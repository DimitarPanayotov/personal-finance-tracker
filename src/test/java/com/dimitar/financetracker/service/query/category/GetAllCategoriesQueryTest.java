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
class GetAllCategoriesQueryTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    private GetAllCategoriesQuery query;

    @BeforeEach
    void setUp() {
        query = new GetAllCategoriesQuery(authenticationFacade, categoryRepository, categoryMapper);
    }

    @Test
    void execute_returnsMappedCategoriesForAuthenticatedUser() {
        Long userId = 42L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Category c1 = Category.builder().id(1L).name("Food").type(CategoryType.EXPENSE).build();
        Category c2 = Category.builder().id(2L).name("Salary").type(CategoryType.INCOME).build();
        when(categoryRepository.findAllByUserId(userId)).thenReturn(List.of(c1, c2));

        CategoryResponse r1 = CategoryResponse.builder().id(1L).name("Food").type(CategoryType.EXPENSE).build();
        CategoryResponse r2 = CategoryResponse.builder().id(2L).name("Salary").type(CategoryType.INCOME).build();
        when(categoryMapper.toResponse(c1)).thenReturn(r1);
        when(categoryMapper.toResponse(c2)).thenReturn(r2);

        List<CategoryResponse> result = query.execute(null);

        assertEquals(List.of(r1, r2), result);
        verify(categoryRepository).findAllByUserId(userId);
        verify(categoryMapper).toResponse(c1);
        verify(categoryMapper).toResponse(c2);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }
}

