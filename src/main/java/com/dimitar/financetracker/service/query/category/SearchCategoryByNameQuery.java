package com.dimitar.financetracker.service.query.category;

import com.dimitar.financetracker.dto.mapper.CategoryMapper;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.query.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchCategoryByNameQuery implements Query<String, List<CategoryResponse>> {
    private final AuthenticationFacade authenticationFacade;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> execute(String input) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();
        List<Category> categories = categoryRepository.findAllByUserIdAndNameContaining(authenticatedUserId, input);
        return categories.stream()
            .map(categoryMapper::toResponse)
            .toList();
    }
}
