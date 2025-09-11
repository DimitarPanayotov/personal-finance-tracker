package com.dimitar.financetracker.service.template;

import com.dimitar.financetracker.model.CategoryType;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DefaultCategoryTemplateService {

    public List<CategoryTemplate> getAllDefaultCategories() {
        return Arrays.asList(
                // Income categories
                new CategoryTemplate("Salary", CategoryType.INCOME, "#4CAF50"),
                new CategoryTemplate("Freelance", CategoryType.INCOME, "#8BC34A"),
                new CategoryTemplate("Investment Returns", CategoryType.INCOME, "#CDDC39"),
                new CategoryTemplate("Rental Income", CategoryType.INCOME, "#FFC107"),
                new CategoryTemplate("Business Income", CategoryType.INCOME, "#FF9800"),
                new CategoryTemplate("Other Income", CategoryType.INCOME, "#795548"),

                // Expense categories
                new CategoryTemplate("Food & Dining", CategoryType.EXPENSE, "#F44336"),
                new CategoryTemplate("Groceries", CategoryType.EXPENSE, "#E91E63"),
                new CategoryTemplate("Transportation", CategoryType.EXPENSE, "#9C27B0"),
                new CategoryTemplate("Utilities", CategoryType.EXPENSE, "#673AB7"),
                new CategoryTemplate("Housing", CategoryType.EXPENSE, "#3F51B5"),
                new CategoryTemplate("Healthcare", CategoryType.EXPENSE, "#2196F3"),
                new CategoryTemplate("Entertainment", CategoryType.EXPENSE, "#03DAC6"),
                new CategoryTemplate("Shopping", CategoryType.EXPENSE, "#00BCD4"),
                new CategoryTemplate("Education", CategoryType.EXPENSE, "#009688"),
                new CategoryTemplate("Insurance", CategoryType.EXPENSE, "#4CAF50"),
                new CategoryTemplate("Travel", CategoryType.EXPENSE, "#8BC34A"),
                new CategoryTemplate("Personal Care", CategoryType.EXPENSE, "#CDDC39"),
                new CategoryTemplate("Gifts & Donations", CategoryType.EXPENSE, "#FFC107"),
                new CategoryTemplate("Subscriptions", CategoryType.EXPENSE, "#FF9800"),
                new CategoryTemplate("Fees & Charges", CategoryType.EXPENSE, "#FF5722"),
                new CategoryTemplate("Other Expenses", CategoryType.EXPENSE, "#795548")
        );
    }
}
