package com.example.finance.finance_backend.Config;

import java.util.LinkedHashMap;
import java.util.Map;

import com.example.finance.finance_backend.Model.Category;

public final class MerchantCategoryRules {
    public MerchantCategoryRules() {
        // Prevent instantiation
    }

    public static final Map<String, Category> RULES = new LinkedHashMap<>();

    static {
        // FOOD
        RULES.put("K-MARKET", Category.FOOD);
        RULES.put("S-MARKET", Category.FOOD);
        RULES.put("LIDL", Category.FOOD);
        RULES.put("ALEPA", Category.FOOD);
        RULES.put("PRISMA", Category.FOOD);
        RULES.put("CITYMARKET", Category.FOOD);
        RULES.put("MCDONALDS", Category.FOOD);
        RULES.put("BURGER KING", Category.FOOD);
        RULES.put("SUBWAY", Category.FOOD);
        RULES.put("PIZZA HUT", Category.FOOD);

        // TRANSPORTATION
        RULES.put("TESLA", Category.TRANSPORTATION);
        RULES.put("VOLKSWAGEN", Category.TRANSPORTATION);
        RULES.put("HSL", Category.TRANSPORTATION);
        RULES.put("VR", Category.TRANSPORTATION);
        RULES.put("FINNAIR", Category.TRANSPORTATION);
        RULES.put("SHELL", Category.TRANSPORTATION);
        RULES.put("ST1", Category.TRANSPORTATION);
        RULES.put("ABC", Category.TRANSPORTATION);
        RULES.put("BOLT", Category.TRANSPORTATION);
        RULES.put("UBER", Category.TRANSPORTATION);
        RULES.put("LYFT", Category.TRANSPORTATION);

        // ENTERTAINMENT
        RULES.put("NETFLIX", Category.ENTERTAINMENT);
        RULES.put("SPOTIFY", Category.ENTERTAINMENT);
        RULES.put("YOUTUBE", Category.ENTERTAINMENT);
        RULES.put("TWITCH", Category.ENTERTAINMENT);
        RULES.put("HBO", Category.ENTERTAINMENT);
        RULES.put("CINEMA", Category.ENTERTAINMENT);
        RULES.put("THEATER", Category.ENTERTAINMENT);
        RULES.put("CONCERT", Category.ENTERTAINMENT);
        RULES.put("AMUSEMENT PARK", Category.ENTERTAINMENT);
        RULES.put("MUSEUM", Category.ENTERTAINMENT);
        RULES.put("GAMING", Category.ENTERTAINMENT);
        RULES.put("SPORTS", Category.ENTERTAINMENT);

        // SHOPPING
        RULES.put("AMAZON", Category.SHOPPING);
        RULES.put("EBAY", Category.SHOPPING);
        RULES.put("ALIEXPRESS", Category.SHOPPING);
        RULES.put("WALMART", Category.SHOPPING);
        RULES.put("VERKKOKAUPPA", Category.SHOPPING);

        // HEALTHCARE
        RULES.put("DOCTOR", Category.HEALTHCARE);
        RULES.put("PHARMACY", Category.HEALTHCARE);

        // EDUCATION
        RULES.put("UNIVERSITY", Category.EDUCATION);

        // INCOME
        RULES.put("SALARY", Category.SALARY);

        // TRANSFER
        RULES.put("TRANSFER", Category.TRANSFER);

        RULES.put("RENT", Category.HOUSING);
        RULES.put("ELECTRICITY", Category.UTILITIES);
        RULES.put("DOCTOR", Category.HEALTHCARE);
        RULES.put("SPA", Category.PERSONAL_CARE);
        RULES.put("SALARY", Category.SALARY);
        RULES.put("AIRBNB", Category.TRAVEL);
        RULES.put("AMAZON", Category.SHOPPING);
        RULES.put("TRANSFER", Category.TRANSFER);
        RULES.put("OTHER", Category.OTHER);
    }
}
