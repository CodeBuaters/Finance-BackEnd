package com.example.finance.finance_backend.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.finance.finance_backend.Model.Budget;
import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Model.Category;
import com.example.finance.finance_backend.Repository.BudgetRepository;
import com.example.finance.finance_backend.Repository.CategoryRepository;
import com.example.finance.finance_backend.Repository.UserRepository;
import com.example.finance.finance_backend.Request.BudgetRequest;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public BudgetService(
            BudgetRepository budgetRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository) {

        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Budget> getBudgets(String email) {
        User user = getUser(email);
        return budgetRepository.findByUserId(user.getId());
    }

    public Budget getBudget(String email, Long id) {
        User user = getUser(email);
        return findUserBudget(user, id);
    }

    public Budget createBudget(String email, BudgetRequest request) {
        User user = getUser(email);

        Long categoryId = request.getCategory().getId();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found"));

        boolean available = category.getUser() == null
                || category.getUser().getId().equals(user.getId());

        if (!available) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Category is not available to this user");
        }

        checkDuplicate(user, category, request.getMonth());

        Budget budget = new Budget();
        budget.setCategory(category);
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setUser(user);

        return budgetRepository.save(budget);
    }

    public Budget updateBudget(String email, Long id, BudgetRequest request) {
        User user = getUser(email);
        Budget budget = findUserBudget(user, id);

        Long categoryId = request.getCategory().getId();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found"));

        boolean available = category.getUser() == null
                || category.getUser().getId().equals(user.getId());

        if (!available) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Category is not available to this user");
        }

        // Check duplicates only if category or month changes
        boolean categoryChanged = !budget.getCategory().getId().equals(category.getId());

        boolean monthChanged = !budget.getMonth().equals(request.getMonth());

        if (categoryChanged || monthChanged) {
            if (budgetRepository.existsByUserIdAndCategoryAndMonth(
                    user.getId(), category, request.getMonth())) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Budget for this category and month already exists");
            }
        }

        budget.setCategory(category);
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());

        return budgetRepository.save(budget);
    }

    public void deleteBudget(String email, Long id) {
        User user = getUser(email);
        Budget budget = findUserBudget(user, id);
        budgetRepository.delete(budget);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    // Returns 404 also when the budget belongs to another user
    private Budget findUserBudget(User user, Long id) {
        return budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget not found"));
    }

    private void checkDuplicate(User user, Category category, String month) {
        if (budgetRepository.existsByUserIdAndCategoryAndMonth(
                user.getId(), category, month)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Budget for this category and month already exists");
        }
    }
}
