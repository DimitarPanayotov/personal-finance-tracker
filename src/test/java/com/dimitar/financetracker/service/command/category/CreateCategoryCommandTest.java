package com.dimitar.financetracker.service.command.category;

import com.dimitar.financetracker.dto.mapper.CategoryMapper;
import com.dimitar.financetracker.dto.request.category.CreateCategoryRequest;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCategoryCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    private CreateCategoryCommand command;

    @BeforeEach
    void setUp() {
        command = new CreateCategoryCommand(authenticationFacade, categoryRepository, categoryMapper);
    }

    @Test
    void execute_createsCategory_forAuthenticatedUser() {
        // Arrange
        User user = User.builder().id(1L).username("john").build();
        when(authenticationFacade.getAuthenticatedUser()).thenReturn(user);

        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Groceries")
                .type(CategoryType.EXPENSE)
                .color("#FF0000")
                .build();

        Category mapped = Category.builder().name("Groceries").type(CategoryType.EXPENSE).color("#FF0000").user(user).build();
        when(categoryMapper.toEntity(request, user)).thenReturn(mapped);

        when(categoryRepository.save(mapped)).thenReturn(mapped);

        CategoryResponse expected = CategoryResponse.builder().id(10L).name("Groceries").type(CategoryType.EXPENSE).color("#FF0000").build();
        when(categoryMapper.toResponse(mapped)).thenReturn(expected);

        // Act
        CategoryResponse actual = command.execute(request);

        // Assert
        assertEquals(expected, actual);
        verify(authenticationFacade).getAuthenticatedUser();
        verify(categoryMapper).toEntity(request, user);
        verify(categoryRepository).save(mapped);
        verify(categoryMapper).toResponse(mapped);
        verifyNoMoreInteractions(authenticationFacade, categoryRepository, categoryMapper);
    }
}
