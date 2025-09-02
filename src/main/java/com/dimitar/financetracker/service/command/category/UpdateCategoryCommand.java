package com.dimitar.financetracker.service.command.category;

import com.dimitar.financetracker.dto.mapper.CategoryMapper;
import com.dimitar.financetracker.dto.response.category.CategoryResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class UpdateCategoryCommand implements Command<UpdateCategoryCommandInput, CategoryResponse> {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse execute(UpdateCategoryCommandInput input) {
        Category category = categoryRepository.findByIdAndUserId(input.categoryId(), input.userId())
            .orElseThrow(() -> new CategoryDoesNotExistException("Category not found or access denied!"));

        categoryMapper.updateEntity(category, input.request());

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }
}
