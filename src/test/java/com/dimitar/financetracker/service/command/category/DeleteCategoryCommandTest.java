package com.dimitar.financetracker.service.command.category;

import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCategoryCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private CategoryRepository categoryRepository;

    private DeleteCategoryCommand command;

    @BeforeEach
    void setUp() {
        command = new DeleteCategoryCommand(authenticationFacade, categoryRepository);
    }

    @Test
    void execute_deletesCategory_whenFoundForUser() {
        Long userId = 5L;
        Long categoryId = 55L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Category cat = Category.builder().id(categoryId).build();
        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.of(cat));

        command.execute(categoryId);

        verify(categoryRepository).delete(cat);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void execute_throwsWhenCategoryNotFound() {
        Long userId = 5L;
        Long categoryId = 123L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.empty());

        assertThrows(CategoryDoesNotExistException.class, () -> command.execute(categoryId));
        verify(categoryRepository, never()).delete(any());
    }
}

