package com.dimitar.financetracker.service.query.category;

import com.dimitar.financetracker.dto.mapper.CategoryMapper;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.query.Query;
import com.dimitar.financetracker.service.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetCategoryByTypeQuery implements Query<CategoryType, List<CategoryResponse>> {
    private final AuthenticationFacade authenticationFacade;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> execute(CategoryType input) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();
        List<Category> categories = categoryRepository.findAllByUserIdAndType(authenticatedUserId, input);
        return categories.stream()
            .map(categoryMapper::toResponse)
            .toList();
    }
}
