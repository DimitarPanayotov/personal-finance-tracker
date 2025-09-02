package com.dimitar.financetracker.service.command.category;

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
public class DeleteCategoryCommand implements Command<DeleteCategoryCommandInput, Void> {
    private final CategoryRepository categoryRepository;

    @Override
    public Void execute(DeleteCategoryCommandInput input) {
        Category category = categoryRepository.findByIdAndUserId(input.categoryId(), input.userId())
            .orElseThrow(() -> new CategoryDoesNotExistException("Category not found or access denied!"));

        categoryRepository.delete(category);
        return null;
    }
}
