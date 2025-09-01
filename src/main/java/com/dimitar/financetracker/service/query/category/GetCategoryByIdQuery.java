package com.dimitar.financetracker.service.query.category;

import com.dimitar.financetracker.dto.mapper.CategoryMapper;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.query.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetCategoryByIdQuery implements Query<Long, CategoryResponse> {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse execute(Long input) {
        Category category = categoryRepository.findById(input)
            .orElseThrow(() -> new CategoryDoesNotExistException("Category with this id does not exist: " + input));

        return categoryMapper.toResponse(category);
    }
}
