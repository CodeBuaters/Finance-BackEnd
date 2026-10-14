package com.example.finance.finance_backend.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.finance.finance_backend.Model.Budget;
import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.BudgetRepository;
import com.example.finance.finance_backend.Repository.UserRepository;
import com.example.finance.finance_backend.Request.BudgetRequest;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public BudgetService(BudgetRepository budgetRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
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
        checkDuplicate(user, request);

        Budget budget = new Budget();
        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setUser(user);
        return budgetRepository.save(budget);
    }

    public Budget updateBudget(String email, Long id, BudgetRequest request) {
        User user = getUser(email);
        Budget budget = findUserBudget(user, id);

        // Check duplicates only if category or month changes
        if (budget.getCategory() != request.getCategory() || !budget.getMonth().equals(request.getMonth())) {
            checkDuplicate(user, request);
        }

        budget.setCategory(request.getCategory());
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

    private void checkDuplicate(User user, BudgetRequest request) {
        if (budgetRepository.existsByUserIdAndCategoryAndMonth(user.getId(), request.getCategory(),
                request.getMonth())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Budget for this category and month already exists");
        }
    }
}
