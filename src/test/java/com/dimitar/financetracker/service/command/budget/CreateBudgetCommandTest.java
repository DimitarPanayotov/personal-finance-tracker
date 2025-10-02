package com.dimitar.financetracker.service.command.budget;

import com.dimitar.financetracker.dto.mapper.BudgetMapper;
import com.dimitar.financetracker.dto.request.budget.CreateBudgetRequest;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.model.BudgetPeriod;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBudgetCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private BudgetMapper budgetMapper;

    private CreateBudgetCommand command;

    @BeforeEach
    void setUp() { command = new CreateBudgetCommand(authenticationFacade, categoryRepository, budgetRepository, budgetMapper); }

    @Test
    void execute_createsBudget_whenCategoryOwnedByUser() {
        User user = User.builder().id(1L).username("john").build();
        when(authenticationFacade.getAuthenticatedUser()).thenReturn(user);

        Category category = Category.builder().id(10L).name("Food").build();
        when(categoryRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(category));

        CreateBudgetRequest req = CreateBudgetRequest.builder()
                .categoryId(10L)
                .amount(new BigDecimal("100.00"))
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.of(2025,1,1))
                .endDate(LocalDate.of(2025,1,31))
                .build();

        Budget mapped = Budget.builder().user(user).category(category).amount(new BigDecimal("100.00")).period(BudgetPeriod.MONTHLY).startDate(LocalDate.of(2025,1,1)).endDate(LocalDate.of(2025,1,31)).isActive(true).build();
        when(budgetMapper.toEntity(req, user, category)).thenReturn(mapped);
        when(budgetRepository.save(mapped)).thenReturn(mapped);

        BudgetResponse expected = BudgetResponse.builder().id(99L).userId(1L).categoryId(10L).amount(new BigDecimal("100.00")).period(BudgetPeriod.MONTHLY).build();
        when(budgetMapper.toResponse(mapped)).thenReturn(expected);

        BudgetResponse result = command.execute(req);
        assertEquals(expected, result);
        verify(categoryRepository).findByIdAndUserId(10L, 1L);
        verify(budgetMapper).toEntity(req, user, category);
        verify(budgetRepository).save(mapped);
        verify(budgetMapper).toResponse(mapped);
    }

    @Test
    void execute_throwsWhenCategoryNotFoundOrNotOwned() {
        User user = User.builder().id(2L).build();
        when(authenticationFacade.getAuthenticatedUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUserId(123L, 2L)).thenReturn(Optional.empty());

        CreateBudgetRequest req = CreateBudgetRequest.builder().categoryId(123L).build();
        assertThrows(CategoryDoesNotExistException.class, () -> command.execute(req));
        verifyNoInteractions(budgetMapper);
        verify(budgetRepository, never()).save(any());
    }
}

