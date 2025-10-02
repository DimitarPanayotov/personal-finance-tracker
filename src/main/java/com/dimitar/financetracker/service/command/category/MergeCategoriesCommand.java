package com.dimitar.financetracker.service.command.category;

import com.dimitar.financetracker.dto.request.category.MergeCategoriesRequest;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.command.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MergeCategoriesCommand implements Command<MergeCategoriesRequest, Void> {
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final AuthenticationFacade authenticationFacade;

    @Override
    @Transactional
    public Void execute(MergeCategoriesRequest request) {
        Long userId = authenticationFacade.getAuthenticatedUserId();

        Category targetCategory = validateAndGetTargetCategory(request.getTargetCategoryId(), userId);

        List<Category> sourceCategories = validateAndGetSourceCategories(
                request.getSourceCategoryIds(), userId, targetCategory);

        transferTransactions(sourceCategories, targetCategory, userId);

        categoryRepository.deleteAll(sourceCategories);

        return null;
    }

    private Category validateAndGetTargetCategory(Long targetCategoryId, Long userId) {
        return categoryRepository.findByIdAndUserId(targetCategoryId, userId)
                .orElseThrow(() -> new CategoryDoesNotExistException("Target category not found"));
    }

    private List<Category> validateAndGetSourceCategories(List<Long> sourceCategoryIds,
                                                         Long userId,
                                                         Category targetCategory) {
        return sourceCategoryIds.stream()
                .map(categoryId -> validateSourceCategory(categoryId, userId, targetCategory))
                .toList();
    }

    private Category validateSourceCategory(Long categoryId, Long userId, Category targetCategory) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new CategoryDoesNotExistException(
                        "Source category with ID " + categoryId + " not found"));

        if (!category.getType().equals(targetCategory.getType())) {
            throw new IllegalArgumentException("Cannot merge categories of different types. Category " +
                    categoryId + " is " + category.getType() + " but target is " + targetCategory.getType());
        }

        if (category.getId().equals(targetCategory.getId())) {
            throw new IllegalArgumentException("Cannot merge category with itself");
        }

        return category;
    }

    private void transferTransactions(List<Category> sourceCategories,
                                    Category targetCategory,
                                    Long userId) {
        for (Category sourceCategory : sourceCategories) {
            List<Transaction> transactionsToTransfer =
                    transactionRepository.findByUserIdAndCategoryId(userId, sourceCategory.getId());

            for (Transaction transaction : transactionsToTransfer) {
                transaction.setCategory(targetCategory);
                transactionRepository.save(transaction);
            }
        }
    }
}
