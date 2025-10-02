package com.dimitar.financetracker.service.command.category;

import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.command.Command;
import com.dimitar.financetracker.service.template.DefaultCategoryTemplateService;
import com.dimitar.financetracker.exception.user.UserDoesNotExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ImportDefaultCategoriesCommand implements Command<Void, List<Category>> {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final DefaultCategoryTemplateService templateService;

    @Override
    @Transactional
    public List<Category> execute(Void input) {
        Long userId = authenticationFacade.getAuthenticatedUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("User not found"));

        List<Category> existingCategories = categoryRepository.findByUserId(userId);
        if (!existingCategories.isEmpty()) {
            throw new IllegalStateException("User already has categories. " +
                    "Cannot import default categories when categories already exist.");
        }

        List<Category> defaultCategories = templateService.getAllDefaultCategories()
                .stream()
                .map(template -> template.toCategory(user))
                .toList();

        return categoryRepository.saveAll(defaultCategories);
    }
}
