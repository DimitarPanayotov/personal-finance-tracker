package com.dimitar.financetracker.util;

public final class ErrorMessages {
    public static final String USERNAME_REQUIRED = "Username is required";
    public static final String USERNAME_TOO_LONG = "Username must be less than %d characters";
    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String EMAIL_INVALID = "Email must be valid";
    public static final String EMAIL_TOO_LONG = "Email must be less than %d characters";
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String PASSWORD_TOO_SHORT = "Password must be at least %d characters";

    public static final String CATEGORY_NAME_REQUIRED = "Category name is required";
    public static final String CATEGORY_NAME_TOO_LONG = "Category name must be less than %d characters";
    public static final String CATEGORY_TYPE_REQUIRED = "Category type is required";
    public static final String CATEGORY_COLOR_REQUIRED = "Color is required";
    public static final String CATEGORY_COLOR_TOO_LONG = "Color must be less than %d characters";
    public static final String USER_REQUIRED = "User is required";

    public static final String AMOUNT_REQUIRED = "Amount is required";
    public static final String AMOUNT_MIN = "Amount must be at least 0.01";
    public static final String TRANSACTION_DATE_REQUIRED = "Transaction date is required";
    public static final String DESCRIPTION_TOO_LONG = "Description must be less than %d characters";
    public static final String CATEGORY_REQUIRED = "Category is required";

    public static final String BUDGET_AMOUNT_REQUIRED = "Budget amount is required";
    public static final String BUDGET_AMOUNT_MIN = "Budget amount must be at least 0.01";
    public static final String START_DATE_REQUIRED = "Start date is required";
    public static final String END_DATE_REQUIRED = "End date is required";
    public static final String BUDGET_PERIOD_REQUIRED = "Budget period is required";
    public static final String BUDGET_NOT_FOUND = "Budget not found with id: %s";

    public static final String USERNAME_OR_EMAIL_REQUIRED = "Username or email required";
    public static final String CURRENT_PASSWORD_REQUIRED = "Current password required";


    public static final String CATEGORY_NOT_FOUND = "Category not found with id: %s";

    private ErrorMessages() {
        throw new AssertionError("Cannot instantiate utility class!");
    }

    public static String format(String template, Object... args) {
        return String.format(template, args);
    }
}