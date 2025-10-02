package com.dimitar.financetracker.service.command.category;

import com.dimitar.financetracker.dto.mapper.CategoryMapper;
import com.dimitar.financetracker.dto.request.category.UpdateCategoryRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    private UpdateCategoryCommand command;

    @BeforeEach
    void setUp() {
        command = new UpdateCategoryCommand(authenticationFacade, categoryRepository, categoryMapper);
    }

    @Test
    void execute_updatesCategory_whenFound() {
        // Arrange
        Long userId = 1L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Category existing = Category.builder().id(100L).name("Old").type(CategoryType.EXPENSE).color("#000").build();
        when(categoryRepository.findByIdAndUserId(100L, userId)).thenReturn(Optional.of(existing));

        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .categoryId(100L)
                .name("New")
                .type(CategoryType.INCOME)
                .color("#123456")
                .build();

        // Save returns the same instance after mapper updates
        when(categoryRepository.save(existing)).thenReturn(existing);

        CategoryResponse expected = CategoryResponse.builder().id(100L).name("New").type(CategoryType.INCOME).color("#123456").build();
        when(categoryMapper.toResponse(existing)).thenReturn(expected);

        // Act
        CategoryResponse actual = command.execute(request);

        // Assert
        verify(categoryMapper).updateEntity(existing, request);
        verify(categoryRepository).save(existing);
        assertEquals(expected, actual);
    }

    @Test
    void execute_throwsWhenCategoryNotFound() {
        Long userId = 1L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(categoryRepository.findByIdAndUserId(200L, userId)).thenReturn(Optional.empty());

        UpdateCategoryRequest request = UpdateCategoryRequest.builder().categoryId(200L).build();

        assertThrows(CategoryDoesNotExistException.class, () -> command.execute(request));
        verify(categoryRepository, never()).save(any());
        verifyNoInteractions(categoryMapper);
    }
}
