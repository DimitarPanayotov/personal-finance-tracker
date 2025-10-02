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
import java.util.Set;
import java.util.stream.Collectors;

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

        // Build a normalized set of existing category names to avoid duplicates (case-insensitive, trimmed)
        Set<String> existingNames = categoryRepository.findByUserId(userId).stream()
                .map(Category::getName)
                .filter(n -> n != null)
                .map(n -> n.trim().toLowerCase())
                .collect(Collectors.toSet());

        List<Category> categoriesToImport = templateService.getAllDefaultCategories().stream()
                .filter(template -> template.getName() != null)
                .filter(template -> !existingNames.contains(template.getName().trim().toLowerCase()))
                .map(template -> template.toCategory(user))
                .toList();

        if (categoriesToImport.isEmpty()) {
            return List.of();
        }

        return categoryRepository.saveAll(categoriesToImport);
    }
}
