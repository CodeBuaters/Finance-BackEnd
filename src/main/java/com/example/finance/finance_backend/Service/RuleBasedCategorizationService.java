package com.example.finance.finance_backend.Service;

import org.springframework.stereotype.Service;

import com.example.finance.finance_backend.Config.MerchantCategoryRules;
import com.example.finance.finance_backend.Model.Category;

@Service
public class RuleBasedCategorizationService {

    public Category categorize(String merchant) {
        if (merchant == null || merchant.isEmpty()) {
            return Category.OTHER;
        }

        String normalizedMerchant = merchant.toUpperCase();

        return MerchantCategoryRules.RULES.entrySet().stream()
                .filter(rule -> normalizedMerchant.contains(rule.getKey().toUpperCase()))
                .map(rule -> rule.getValue())
                .findFirst()
                .orElse(Category.OTHER);
    }
}