package com.example.finance.finance_backend.Service;

import org.springframework.stereotype.Service;

import com.example.finance.finance_backend.Config.MerchantCategoryRules;
import com.example.finance.finance_backend.Model.Category;
import com.example.finance.finance_backend.Repository.CategoryRepository;

@Service
public class RuleBasedCategorizationService {

    private final CategoryRepository categoryRepository;

    public RuleBasedCategorizationService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category categorize(String merchant) {
        if (merchant == null || merchant.isEmpty()) {
            return categoryRepository.findByNameAndUserIsNull("Other")
                    .orElse(null);
        }

        String normalizedMerchant = merchant.toUpperCase();

        return MerchantCategoryRules.RULES.entrySet().stream()
                .filter(rule -> normalizedMerchant.contains(rule.getKey()))
                .map(rule -> categoryRepository
                        .findByNameAndUserIsNull(rule.getValue())
                        .orElse(null))
                .findFirst()
                .orElseGet(() -> categoryRepository
                        .findByNameAndUserIsNull("Other")
                        .orElse(null));

    }
}