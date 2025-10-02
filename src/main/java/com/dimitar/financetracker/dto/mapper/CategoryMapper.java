package com.dimitar.financetracker.dto.mapper;

import com.dimitar.financetracker.dto.request.category.CreateCategoryRequest;
import com.dimitar.financetracker.dto.request.category.UpdateCategoryRequest;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.dto.response.category.CategorySummaryResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoryMapper {
    public Category toEntity(CreateCategoryRequest request, User user) {
        if (request == null) {
            return null;
        }

        return Category.builder()
            .user(user)
            .name(request.getName())
            .type(request.getType())
            .color(request.getColor())
            .build();
    }

    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryResponse.builder()
            .id(category.getId())
            .name(category.getName())
            .type(category.getType())
            .color(category.getColor())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }

    public CategorySummaryResponse toSummaryResponse(Category category) {
        if (category == null) {
            return null;
        }
        return CategorySummaryResponse.builder()
            .id(category.getId())
            .name(category.getName())
            .type(category.getType())
            .color(category.getColor())
            .build();
    }

    public void updateEntity(Category category, UpdateCategoryRequest request) {
        if (category == null || request == null) {
            return;
        }

        if (request.getName() != null) {
            category.setName(request.getName());
        }

        if (request.getType() != null) {
            category.setType(request.getType());
        }

        if (request.getColor() != null) {
            category.setColor(request.getColor());
        }

    }
}
