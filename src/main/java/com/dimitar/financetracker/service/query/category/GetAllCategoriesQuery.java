package com.dimitar.financetracker.service.query.category;

import com.dimitar.financetracker.dto.mapper.CategoryMapper;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.query.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllCategoriesQuery implements Query<Long, List<CategoryResponse>> {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    @Override
    public List<CategoryResponse> execute(Long userId) {
        List<Category> categories = categoryRepository.findAllByUserId(userId);
        return categories.stream()
            .map(categoryMapper::toResponse)
            .toList();
    }
}
