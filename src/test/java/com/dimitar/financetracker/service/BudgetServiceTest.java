package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.budget.CreateBudgetRequest;
import com.dimitar.financetracker.dto.request.budget.UpdateBudgetRequest;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.dto.response.budget.BudgetUsageResponse;
import com.dimitar.financetracker.model.BudgetPeriod;
import com.dimitar.financetracker.service.command.budget.ActivateBudgetCommand;
import com.dimitar.financetracker.service.command.budget.CreateBudgetCommand;
import com.dimitar.financetracker.service.command.budget.DeactivateBudgetCommand;
import com.dimitar.financetracker.service.command.budget.DeleteBudgetCommand;
import com.dimitar.financetracker.service.command.budget.UpdateBudgetCommand;
import com.dimitar.financetracker.service.query.budget.GetActiveBudgetsQuery;
import com.dimitar.financetracker.service.query.budget.GetAllBudgetsQuery;
import com.dimitar.financetracker.service.query.budget.GetAllBudgetsUsageQuery;
import com.dimitar.financetracker.service.query.budget.GetBudgetByIdQuery;
import com.dimitar.financetracker.service.query.budget.GetBudgetsByCategoryQuery;
import com.dimitar.financetracker.service.query.budget.GetBudgetUsageQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock private CreateBudgetCommand createBudgetCommand;
    @Mock private GetAllBudgetsQuery getAllBudgetsQuery;
    @Mock private UpdateBudgetCommand updateBudgetCommand;
    @Mock private DeleteBudgetCommand deleteBudgetCommand;
    @Mock private DeactivateBudgetCommand deactivateBudgetCommand;
    @Mock private ActivateBudgetCommand activateBudgetCommand;
    @Mock private GetBudgetByIdQuery getBudgetByIdQuery;
    @Mock private GetBudgetsByCategoryQuery getBudgetsByCategoryQuery;
    @Mock private GetBudgetUsageQuery getBudgetUsageQuery;
    @Mock private GetAllBudgetsUsageQuery getAllBudgetsUsageQuery;
    @Mock private GetActiveBudgetsQuery getActiveBudgetsQuery;

    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        budgetService = new BudgetService(
                createBudgetCommand,
                getAllBudgetsQuery,
                updateBudgetCommand,
                deleteBudgetCommand,
                deactivateBudgetCommand,
                activateBudgetCommand,
                getBudgetByIdQuery,
                getBudgetsByCategoryQuery,
                getBudgetUsageQuery,
                getAllBudgetsUsageQuery,
                getActiveBudgetsQuery
        );
    }

    @Test
    void createBudget_delegatesToCommand() {
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .categoryId(1L)
                .amount(BigDecimal.valueOf(100))
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 1, 31))
                .build();
        BudgetResponse expected = BudgetResponse.builder().id(1L).amount(BigDecimal.valueOf(100)).build();
        when(createBudgetCommand.execute(request)).thenReturn(expected);

        BudgetResponse actual = budgetService.createBudget(request);

        assertEquals(expected, actual);
        verify(createBudgetCommand).execute(request);
        verifyNoMoreInteractions(createBudgetCommand);
        verifyNoInteractions(getAllBudgetsQuery, updateBudgetCommand, deleteBudgetCommand, deactivateBudgetCommand,
                activateBudgetCommand, getBudgetByIdQuery, getBudgetsByCategoryQuery, getBudgetUsageQuery,
                getAllBudgetsUsageQuery, getActiveBudgetsQuery);
    }

    @Test
    void getAllBudgets_delegatesToQuery() {
        List<BudgetResponse> expected = List.of(BudgetResponse.builder().id(1L).build());
        when(getAllBudgetsQuery.execute(null)).thenReturn(expected);

        List<BudgetResponse> actual = budgetService.getAllBudgets();

        assertEquals(expected, actual);
        verify(getAllBudgetsQuery).execute(null);
        verifyNoMoreInteractions(getAllBudgetsQuery);
        verifyNoInteractions(createBudgetCommand, updateBudgetCommand, deleteBudgetCommand, deactivateBudgetCommand,
                activateBudgetCommand, getBudgetByIdQuery, getBudgetsByCategoryQuery, getBudgetUsageQuery,
                getAllBudgetsUsageQuery, getActiveBudgetsQuery);
    }

    @Test
    void updateBudget_delegatesToCommand() {
        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .budgetId(2L)
                .amount(BigDecimal.valueOf(200))
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.of(2025, 2, 1))
                .endDate(LocalDate.of(2025, 2, 28))
                .build();
        BudgetResponse expected = BudgetResponse.builder().id(2L).amount(BigDecimal.valueOf(200)).build();
        when(updateBudgetCommand.execute(request)).thenReturn(expected);

        BudgetResponse actual = budgetService.updateBudget(request);

        assertEquals(expected, actual);
        verify(updateBudgetCommand).execute(request);
        verifyNoMoreInteractions(updateBudgetCommand);
        verifyNoInteractions(createBudgetCommand, getAllBudgetsQuery, deleteBudgetCommand, deactivateBudgetCommand,
                activateBudgetCommand, getBudgetByIdQuery, getBudgetsByCategoryQuery, getBudgetUsageQuery,
                getAllBudgetsUsageQuery, getActiveBudgetsQuery);
    }

    @Test
    void deleteBudget_delegatesToCommand() {
        Long id = 5L;
        budgetService.deleteBudget(id);
        verify(deleteBudgetCommand).execute(id);
        verifyNoMoreInteractions(deleteBudgetCommand);
        verifyNoInteractions(createBudgetCommand, getAllBudgetsQuery, updateBudgetCommand, deactivateBudgetCommand,
                activateBudgetCommand, getBudgetByIdQuery, getBudgetsByCategoryQuery, getBudgetUsageQuery,
                getAllBudgetsUsageQuery, getActiveBudgetsQuery);
    }

    @Test
    void deactivateBudget_delegatesToCommand() {
        Long id = 6L;
        BudgetResponse expected = BudgetResponse.builder().id(id).isActive(false).build();
        when(deactivateBudgetCommand.execute(id)).thenReturn(expected);

        BudgetResponse actual = budgetService.deactivateBudget(id);

        assertEquals(expected, actual);
        verify(deactivateBudgetCommand).execute(id);
        verifyNoMoreInteractions(deactivateBudgetCommand);
        verifyNoInteractions(createBudgetCommand, getAllBudgetsQuery, updateBudgetCommand, deleteBudgetCommand,
                activateBudgetCommand, getBudgetByIdQuery, getBudgetsByCategoryQuery, getBudgetUsageQuery,
                getAllBudgetsUsageQuery, getActiveBudgetsQuery);
    }

    @Test
    void activateBudget_delegatesToCommand() {
        Long id = 7L;
        BudgetResponse expected = BudgetResponse.builder().id(id).isActive(true).build();
        when(activateBudgetCommand.execute(id)).thenReturn(expected);

        BudgetResponse actual = budgetService.activateBudget(id);

        assertEquals(expected, actual);
        verify(activateBudgetCommand).execute(id);
        verifyNoMoreInteractions(activateBudgetCommand);
        verifyNoInteractions(createBudgetCommand, getAllBudgetsQuery, updateBudgetCommand, deleteBudgetCommand,
                deactivateBudgetCommand, getBudgetByIdQuery, getBudgetsByCategoryQuery, getBudgetUsageQuery,
                getAllBudgetsUsageQuery, getActiveBudgetsQuery);
    }

    @Test
    void getBudgetById_delegatesToQuery() {
        Long id = 8L;
        BudgetResponse expected = BudgetResponse.builder().id(id).build();
        when(getBudgetByIdQuery.execute(id)).thenReturn(expected);

        BudgetResponse actual = budgetService.getBudgetById(id);

        assertEquals(expected, actual);
        verify(getBudgetByIdQuery).execute(id);
        verifyNoMoreInteractions(getBudgetByIdQuery);
        verifyNoInteractions(createBudgetCommand, getAllBudgetsQuery, updateBudgetCommand, deleteBudgetCommand,
                deactivateBudgetCommand, activateBudgetCommand, getBudgetsByCategoryQuery, getBudgetUsageQuery,
                getAllBudgetsUsageQuery, getActiveBudgetsQuery);
    }

    @Test
    void getBudgetsByCategory_delegatesToQuery() {
        Long categoryId = 9L;
        List<BudgetResponse> expected = List.of(BudgetResponse.builder().id(1L).build());
        when(getBudgetsByCategoryQuery.execute(categoryId)).thenReturn(expected);

        List<BudgetResponse> actual = budgetService.getBudgetsByCategory(categoryId);

        assertEquals(expected, actual);
        verify(getBudgetsByCategoryQuery).execute(categoryId);
        verifyNoMoreInteractions(getBudgetsByCategoryQuery);
        verifyNoInteractions(createBudgetCommand, getAllBudgetsQuery, updateBudgetCommand, deleteBudgetCommand,
                deactivateBudgetCommand, activateBudgetCommand, getBudgetByIdQuery, getBudgetUsageQuery,
                getAllBudgetsUsageQuery, getActiveBudgetsQuery);
    }

    @Test
    void getBudgetUsage_delegatesToQuery() {
        Long id = 10L;
        BudgetUsageResponse expected = BudgetUsageResponse.builder().id(id).spent(BigDecimal.TEN).build();
        when(getBudgetUsageQuery.execute(id)).thenReturn(expected);

        BudgetUsageResponse actual = budgetService.getBudgetUsage(id);

        assertEquals(expected, actual);
        verify(getBudgetUsageQuery).execute(id);
        verifyNoMoreInteractions(getBudgetUsageQuery);
        verifyNoInteractions(createBudgetCommand, getAllBudgetsQuery, updateBudgetCommand, deleteBudgetCommand,
                deactivateBudgetCommand, activateBudgetCommand, getBudgetByIdQuery, getBudgetsByCategoryQuery,
                getAllBudgetsUsageQuery, getActiveBudgetsQuery);
    }

    @Test
    void getAllBudgetsUsage_delegatesToQuery() {
        List<BudgetUsageResponse> expected = List.of(BudgetUsageResponse.builder().id(1L).build());
        when(getAllBudgetsUsageQuery.execute(null)).thenReturn(expected);

        List<BudgetUsageResponse> actual = budgetService.getAllBudgetsUsage();

        assertEquals(expected, actual);
        verify(getAllBudgetsUsageQuery).execute(null);
        verifyNoMoreInteractions(getAllBudgetsUsageQuery);
        verifyNoInteractions(createBudgetCommand, getAllBudgetsQuery, updateBudgetCommand, deleteBudgetCommand,
                deactivateBudgetCommand, activateBudgetCommand, getBudgetByIdQuery, getBudgetsByCategoryQuery,
                getBudgetUsageQuery, getActiveBudgetsQuery);
    }

    @Test
    void getActiveBudgets_delegatesToQuery() {
        List<BudgetResponse> expected = List.of(BudgetResponse.builder().id(1L).build());
        when(getActiveBudgetsQuery.execute(null)).thenReturn(expected);

        List<BudgetResponse> actual = budgetService.getActiveBudgets();

        assertEquals(expected, actual);
        verify(getActiveBudgetsQuery).execute(null);
        verifyNoMoreInteractions(getActiveBudgetsQuery);
        verifyNoInteractions(createBudgetCommand, getAllBudgetsQuery, updateBudgetCommand, deleteBudgetCommand,
                deactivateBudgetCommand, activateBudgetCommand, getBudgetByIdQuery, getBudgetsByCategoryQuery,
                getBudgetUsageQuery, getAllBudgetsUsageQuery);
    }
}

