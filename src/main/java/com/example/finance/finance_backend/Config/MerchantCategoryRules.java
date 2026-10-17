package com.example.finance.finance_backend.Config;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MerchantCategoryRules {
    public MerchantCategoryRules() {
        // Prevent instantiation
    }

    public static final Map<String, String> RULES = new LinkedHashMap<>();

    static {
        // FOOD
        RULES.put("K-MARKET", "Food");
        RULES.put("S-MARKET", "Food");
        RULES.put("LIDL", "Food");
        RULES.put("ALEPA", "Food");
        RULES.put("PRISMA", "Food");
        RULES.put("CITYMARKET", "Food");
        RULES.put("MCDONALDS", "Food");
        RULES.put("BURGER KING", "Food");
        RULES.put("SUBWAY", "Food");
        RULES.put("PIZZA HUT", "Food");

        // TRANSPORTATION
        RULES.put("TESLA", "Transportation");
        RULES.put("VOLKSWAGEN", "Transportation");
        RULES.put("HSL", "Transportation");
        RULES.put("VR", "Transportation");
        RULES.put("FINNAIR", "Transportation");
        RULES.put("SHELL", "Transportation");
        RULES.put("ST1", "Transportation");
        RULES.put("ABC", "Transportation");
        RULES.put("BOLT", "Transportation");
        RULES.put("UBER", "Transportation");
        RULES.put("LYFT", "Transportation");

        // ENTERTAINMENT
        RULES.put("NETFLIX", "Entertainment");
        RULES.put("SPOTIFY", "Entertainment");
        RULES.put("YOUTUBE", "Entertainment");
        RULES.put("TWITCH", "Entertainment");
        RULES.put("HBO", "Entertainment");
        RULES.put("CINEMA", "Entertainment");
        RULES.put("THEATER", "Entertainment");
        RULES.put("CONCERT", "Entertainment");
        RULES.put("AMUSEMENT PARK", "Entertainment");
        RULES.put("MUSEUM", "Entertainment");
        RULES.put("GAMING", "Entertainment");
        RULES.put("SPORTS", "Entertainment");

        // SHOPPING
        RULES.put("AMAZON", "Shopping");
        RULES.put("EBAY", "Shopping");
        RULES.put("ALIEXPRESS", "Shopping");
        RULES.put("WALMART", "Shopping");
        RULES.put("VERKKOKAUPPA", "Shopping");

        // HEALTHCARE
        RULES.put("DOCTOR", "Healthcare");
        RULES.put("PHARMACY", "Healthcare");

        // EDUCATION
        RULES.put("UNIVERSITY", "Education");

        // INCOME
        RULES.put("SALARY", "Salary");

        // HOUSING
        RULES.put("RENT", "Housing");

        // UTILITIES
        RULES.put("ELECTRICITY", "Utilities");

        // PERSONAL CARE
        RULES.put("SPA", "Personal Care");

        // TRAVEL
        RULES.put("AIRBNB", "Travel");

        // OTHER
        RULES.put("OTHER", "Other");
    }

}
/*
 * static {
 * // FOOD
 * RULES.put("K-MARKET", Category.FOOD);
 * RULES.put("S-MARKET", Category.FOOD);
 * RULES.put("LIDL", Category.FOOD);
 * RULES.put("ALEPA", Category.FOOD);
 * RULES.put("PRISMA", Category.FOOD);
 * RULES.put("CITYMARKET", Category.FOOD);
 * RULES.put("MCDONALDS", Category.FOOD);
 * RULES.put("BURGER KING", Category.FOOD);
 * RULES.put("SUBWAY", Category.FOOD);
 * RULES.put("PIZZA HUT", Category.FOOD);
 * 
 * // TRANSPORTATION
 * RULES.put("TESLA", Category.TRANSPORTATION);
 * RULES.put("VOLKSWAGEN", Category.TRANSPORTATION);
 * RULES.put("HSL", Category.TRANSPORTATION);
 * RULES.put("VR", Category.TRANSPORTATION);
 * RULES.put("FINNAIR", Category.TRANSPORTATION);
 * RULES.put("SHELL", Category.TRANSPORTATION);
 * RULES.put("ST1", Category.TRANSPORTATION);
 * RULES.put("ABC", Category.TRANSPORTATION);
 * RULES.put("BOLT", Category.TRANSPORTATION);
 * RULES.put("UBER", Category.TRANSPORTATION);
 * RULES.put("LYFT", Category.TRANSPORTATION);
 * 
 * // ENTERTAINMENT
 * RULES.put("NETFLIX", Category.ENTERTAINMENT);
 * RULES.put("SPOTIFY", Category.ENTERTAINMENT);
 * RULES.put("YOUTUBE", Category.ENTERTAINMENT);
 * RULES.put("TWITCH", Category.ENTERTAINMENT);
 * RULES.put("HBO", Category.ENTERTAINMENT);
 * RULES.put("CINEMA", Category.ENTERTAINMENT);
 * RULES.put("THEATER", Category.ENTERTAINMENT);
 * RULES.put("CONCERT", Category.ENTERTAINMENT);
 * RULES.put("AMUSEMENT PARK", Category.ENTERTAINMENT);
 * RULES.put("MUSEUM", Category.ENTERTAINMENT);
 * RULES.put("GAMING", Category.ENTERTAINMENT);
 * RULES.put("SPORTS", Category.ENTERTAINMENT);
 * 
 * // SHOPPING
 * RULES.put("AMAZON", Category.SHOPPING);
 * RULES.put("EBAY", Category.SHOPPING);
 * RULES.put("ALIEXPRESS", Category.SHOPPING);
 * RULES.put("WALMART", Category.SHOPPING);
 * RULES.put("VERKKOKAUPPA", Category.SHOPPING);
 * 
 * // HEALTHCARE
 * RULES.put("DOCTOR", Category.HEALTHCARE);
 * RULES.put("PHARMACY", Category.HEALTHCARE);
 * 
 * // EDUCATION
 * RULES.put("UNIVERSITY", Category.EDUCATION);
 * 
 * // INCOME
 * RULES.put("SALARY", Category.SALARY);
 * 
 * RULES.put("RENT", Category.HOUSING);
 * RULES.put("ELECTRICITY", Category.UTILITIES);
 * RULES.put("DOCTOR", Category.HEALTHCARE);
 * RULES.put("SPA", Category.PERSONAL_CARE);
 * RULES.put("SALARY", Category.SALARY);
 * RULES.put("AIRBNB", Category.TRAVEL);
 * RULES.put("AMAZON", Category.SHOPPING);
 * RULES.put("OTHER", Category.OTHER);
 * }
 */