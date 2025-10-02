package com.dimitar.financetracker.service.query.category;

import com.dimitar.financetracker.dto.mapper.CategoryMapper;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCategoryByIdQueryTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    private GetCategoryByIdQuery query;

    @BeforeEach
    void setUp() {
        query = new GetCategoryByIdQuery(authenticationFacade, categoryRepository, categoryMapper);
    }

    @Test
    void execute_returnsMappedCategory_whenFoundForUser() {
        Long userId = 77L;
        Long categoryId = 5L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Category category = Category.builder().id(categoryId).name("Food").type(CategoryType.EXPENSE).build();
        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.of(category));

        CategoryResponse expected = CategoryResponse.builder().id(categoryId).name("Food").type(CategoryType.EXPENSE).build();
        when(categoryMapper.toResponse(category)).thenReturn(expected);

        CategoryResponse result = query.execute(categoryId);

        assertEquals(expected, result);
        verify(categoryRepository).findByIdAndUserId(categoryId, userId);
        verify(categoryMapper).toResponse(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void execute_throwsWhenNotFoundForUser() {
        Long userId = 77L;
        Long categoryId = 99L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.empty());

        assertThrows(CategoryDoesNotExistException.class, () -> query.execute(categoryId));
        verifyNoInteractions(categoryMapper);
    }
}

