package com.dimitar.financetracker.service.template;

import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.model.CategoryType;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultCategoryTemplateService {

    @Data
    public static class CategoryTemplate {
        private final String name;
        private final CategoryType type;
        private final String color;

        public CategoryTemplate(String name, CategoryType type, String color) {
            this.name = name;
            this.type = type;
            this.color = color;
        }

        public Category toCategory(User user) {
            return Category.builder()
                    .name(name)
                    .type(type)
                    .color(color)
                    .user(user)
                    .build();
        }
    }

    public List<CategoryTemplate> getDefaultExpenseCategories() {
        return List.of(
            new CategoryTemplate("Food & Dining", CategoryType.EXPENSE, "#FF6B6B"),
            new CategoryTemplate("Groceries", CategoryType.EXPENSE, "#4ECDC4"),
            new CategoryTemplate("Transportation", CategoryType.EXPENSE, "#45B7D1"),
            new CategoryTemplate("Gas & Fuel", CategoryType.EXPENSE, "#96CEB4"),
            new CategoryTemplate("Entertainment", CategoryType.EXPENSE, "#FECA57"),
            new CategoryTemplate("Shopping", CategoryType.EXPENSE, "#FF9FF3"),
            new CategoryTemplate("Health & Medical", CategoryType.EXPENSE, "#54A0FF"),
            new CategoryTemplate("Insurance", CategoryType.EXPENSE, "#5F27CD"),
            new CategoryTemplate("Utilities", CategoryType.EXPENSE, "#00D2D3"),
            new CategoryTemplate("Rent/Mortgage", CategoryType.EXPENSE, "#FF6348"),
            new CategoryTemplate("Phone & Internet", CategoryType.EXPENSE, "#2ED573"),
            new CategoryTemplate("Education", CategoryType.EXPENSE, "#FFA502"),
            new CategoryTemplate("Travel", CategoryType.EXPENSE, "#3742FA"),
            new CategoryTemplate("Personal Care", CategoryType.EXPENSE, "#F8B500"),
            new CategoryTemplate("Subscriptions", CategoryType.EXPENSE, "#A4B0BE"),
            new CategoryTemplate("Miscellaneous", CategoryType.EXPENSE, "#57606F")
        );
    }

    public List<CategoryTemplate> getDefaultIncomeCategories() {
        return List.of(
            new CategoryTemplate("Salary", CategoryType.INCOME, "#2ED573"),
            new CategoryTemplate("Freelance", CategoryType.INCOME, "#1DD1A1"),
            new CategoryTemplate("Business Income", CategoryType.INCOME, "#00D2D3"),
            new CategoryTemplate("Investment Returns", CategoryType.INCOME, "#55A3FF"),
            new CategoryTemplate("Rental Income", CategoryType.INCOME, "#26DE81"),
            new CategoryTemplate("Side Hustle", CategoryType.INCOME, "#0FB9B1"),
            new CategoryTemplate("Gifts", CategoryType.INCOME, "#A55EEA"),
            new CategoryTemplate("Refunds", CategoryType.INCOME, "#778CA3"),
            new CategoryTemplate("Bonus", CategoryType.INCOME, "#F8B500"),
            new CategoryTemplate("Other Income", CategoryType.INCOME, "#4B6584")
        );
    }

    public List<CategoryTemplate> getAllDefaultCategories() {
        return List.of(
            getDefaultExpenseCategories(),
            getDefaultIncomeCategories()
        ).stream()
        .flatMap(List::stream)
        .toList();
    }
}
