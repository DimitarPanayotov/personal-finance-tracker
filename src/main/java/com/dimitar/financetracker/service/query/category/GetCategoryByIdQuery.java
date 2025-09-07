package com.dimitar.financetracker.service.query.category;

import com.dimitar.financetracker.dto.mapper.CategoryMapper;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.query.Query;
import com.dimitar.financetracker.service.query.category.input.GetCategoryByIdQueryInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetCategoryByIdQuery implements Query<GetCategoryByIdQueryInput, CategoryResponse> {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse execute(GetCategoryByIdQueryInput input) {
        Category category = categoryRepository.findByIdAndUserId(input.categoryId(), input.userId())
            .orElseThrow(() -> new CategoryDoesNotExistException("Category with this id does not exist or does not belong to the user: " + input.categoryId()));

        return categoryMapper.toResponse(category);
    }
}
