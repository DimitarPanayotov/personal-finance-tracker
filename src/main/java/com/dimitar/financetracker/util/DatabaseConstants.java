package com.dimitar.financetracker.util;

public final class DatabaseConstants {
    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int EMAIL_MAX_LENGTH = 100;
    public static final int PASSWORD_MIN_LENGTH = 6;

    public static final int CATEGORY_NAME_MAX_LENGTH = 100;
    public static final int COLOR_LENGTH = 7;

    public static final int DESCRIPTION_MAX_LENGTH = 255;
    public static final int AMOUNT_PRECISION = 10;
    public static final int AMOUNT_SCALE = 2;

    public static final int BUDGET_PERIOD_MAX_LENGTH = 20;

    private DatabaseConstants() {
        throw new AssertionError("Cannot instantiate utility class!");
    }
}
