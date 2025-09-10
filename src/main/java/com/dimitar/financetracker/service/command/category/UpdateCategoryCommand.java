package com.dimitar.financetracker.service.command.category;

import com.dimitar.financetracker.dto.mapper.CategoryMapper;
import com.dimitar.financetracker.dto.request.category.UpdateCategoryRequest;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class UpdateCategoryCommand implements Command<UpdateCategoryRequest, CategoryResponse> {
    private final AuthenticationFacade authenticationFacade;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse execute(UpdateCategoryRequest request) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();
        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), authenticatedUserId)
            .orElseThrow(() -> new CategoryDoesNotExistException("Category not found or access denied!"));

        categoryMapper.updateEntity(category, request);

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }
}
