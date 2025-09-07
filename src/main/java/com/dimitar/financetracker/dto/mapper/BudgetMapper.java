package com.dimitar.financetracker.dto.mapper;

import com.dimitar.financetracker.dto.request.budget.CreateBudgetRequest;
import com.dimitar.financetracker.dto.request.budget.UpdateBudgetRequest;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.dto.response.budget.BudgetSummaryResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {
    public Budget toEntity(CreateBudgetRequest request, User user, Category category) {
        if (request == null) {
            return null;
        }
        return Budget.builder()
            .user(user)
            .category(category)
            .amount(request.getAmount())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .period(request.getPeriod())
            .isActive(true)
            .build();
    }

    public BudgetResponse toResponse(Budget budget) {
        if (budget == null) {
            return null;
        }
        return BudgetResponse.builder()
            .id(budget.getId())
            .userId(budget.getUser().getId())
            .categoryId(budget.getCategory().getId())
            .categoryName(budget.getCategory().getName())
            .amount(budget.getAmount())
            .startDate(budget.getStartDate())
            .endDate(budget.getEndDate())
            .period(budget.getPeriod())
            .isActive(budget.getIsActive())
            .createdAt(budget.getCreatedAt())
            .updatedAt(budget.getUpdatedAt())
            .build();
    }

    public BudgetSummaryResponse toSummaryResponse(Budget budget) {
        if (budget == null) {
            return null;
        }
        return BudgetSummaryResponse.builder()
            .id(budget.getId())
            .userId(budget.getUser().getId())
            .categoryId(budget.getCategory().getId())
            .categoryName(budget.getCategory().getName())
            .amount(budget.getAmount())
            .startDate(budget.getStartDate())
            .endDate(budget.getEndDate())
            .period(budget.getPeriod())
            .isActive(budget.getIsActive())
            .build();
    }

    public void updateEntity(Budget budget, UpdateBudgetRequest request, Category category) {
        if (budget == null || request == null) {
            return;
        }

        if (category != null) {
            budget.setCategory(category);
        }

        if (request.getAmount() != null) {
            budget.setAmount(request.getAmount());
        }

        if (request.getPeriod() != null) {
            budget.setPeriod(request.getPeriod());
        }

        if (request.getStartDate() != null) {
            budget.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            budget.setEndDate(request.getEndDate());
        }
    }
}
