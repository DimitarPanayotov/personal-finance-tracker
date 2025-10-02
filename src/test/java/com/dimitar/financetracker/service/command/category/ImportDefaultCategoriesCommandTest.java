package com.dimitar.financetracker.service.command.category;

import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.user.UserDoesNotExistException;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.template.CategoryTemplate;
import com.dimitar.financetracker.service.template.DefaultCategoryTemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportDefaultCategoriesCommandTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private DefaultCategoryTemplateService templateService;

    private ImportDefaultCategoriesCommand command;

    @BeforeEach
    void setUp() {
        command = new ImportDefaultCategoriesCommand(categoryRepository, userRepository, authenticationFacade, templateService);
    }

    @Test
    void execute_importsDefaults_whenUserHasNoCategories() {
        Long userId = 7L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        User user = User.builder().id(userId).username("john").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(categoryRepository.findByUserId(userId)).thenReturn(List.of());

        List<CategoryTemplate> templates = List.of(
                new CategoryTemplate("Salary", CategoryType.INCOME, "#111111"),
                new CategoryTemplate("Food", CategoryType.EXPENSE, "#222222")
        );
        when(templateService.getAllDefaultCategories()).thenReturn(templates);

        when(categoryRepository.saveAll(any(Iterable.class))).thenAnswer(invocation ->
                StreamSupport.stream(((Iterable<Category>) invocation.getArgument(0)).spliterator(), false).toList()
        );

        List<Category> result = command.execute(null);

        // Verify saved categories content using matcher to handle Iterable
        verify(categoryRepository).saveAll(argThat((Iterable<Category> saved) -> {
            List<Category> list = StreamSupport.stream(saved.spliterator(), false).toList();
            return list.size() == 2
                    && "Salary".equals(list.get(0).getName())
                    && CategoryType.INCOME.equals(list.get(0).getType())
                    && userId.equals(list.get(0).getUser().getId())
                    && "Food".equals(list.get(1).getName())
                    && CategoryType.EXPENSE.equals(list.get(1).getType())
                    && userId.equals(list.get(1).getUser().getId());
        }));

        assertEquals(2, result.size());
        assertEquals("Salary", result.get(0).getName());
        assertEquals("Food", result.get(1).getName());
    }

    @Test
    void execute_throwsWhenUserNotFound() {
        Long userId = 99L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () -> command.execute(null));
        verifyNoInteractions(templateService);
        verify(categoryRepository, never()).saveAll(any(Iterable.class));
    }

    @Test
    void execute_importsOnlyMissing_whenUserAlreadyHasSomeCategories() {
        Long userId = 7L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));

        // Existing already has 'Salary' (case-insensitive match)
        when(categoryRepository.findByUserId(userId)).thenReturn(List.of(
                Category.builder().name("salary").type(CategoryType.INCOME).build()
        ));

        List<CategoryTemplate> templates = List.of(
                new CategoryTemplate("Salary", CategoryType.INCOME, "#111111"),
                new CategoryTemplate("Food", CategoryType.EXPENSE, "#222222")
        );
        when(templateService.getAllDefaultCategories()).thenReturn(templates);

        when(categoryRepository.saveAll(any(Iterable.class))).thenAnswer(invocation ->
                StreamSupport.stream(((Iterable<Category>) invocation.getArgument(0)).spliterator(), false).toList()
        );

        List<Category> result = command.execute(null);

        // Should only import 'Food'
        verify(categoryRepository).saveAll(argThat((Iterable<Category> saved) -> {
            List<Category> list = StreamSupport.stream(saved.spliterator(), false).toList();
            return list.size() == 1
                    && "Food".equals(list.get(0).getName())
                    && CategoryType.EXPENSE.equals(list.get(0).getType())
                    && list.get(0).getUser() != null;
        }));

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getName());
    }

    @Test
    void execute_returnsEmpty_whenAllDefaultsAlreadyPresent() {
        Long userId = 7L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));

        // Existing categories cover the defaults provided by the template
        when(categoryRepository.findByUserId(userId)).thenReturn(List.of(
                Category.builder().name("Salary").type(CategoryType.INCOME).build(),
                Category.builder().name("Food").type(CategoryType.EXPENSE).build()
        ));

        when(templateService.getAllDefaultCategories()).thenReturn(List.of(
                new CategoryTemplate("Salary", CategoryType.INCOME, "#111111"),
                new CategoryTemplate("Food", CategoryType.EXPENSE, "#222222")
        ));

        List<Category> result = command.execute(null);

        // No save when nothing to import
        verify(categoryRepository, never()).saveAll(any(Iterable.class));
        assertTrue(result.isEmpty());
    }
}
