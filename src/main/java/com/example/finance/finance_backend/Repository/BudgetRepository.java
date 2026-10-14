package com.example.finance.finance_backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.finance.finance_backend.Model.Budget;
import com.example.finance.finance_backend.Model.Category;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);

    // Finds the budget only if it belongs to this user
    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndCategoryAndMonth(Long userId, Category category, String month);
}
