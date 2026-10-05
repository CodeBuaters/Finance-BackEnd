package com.example.finance.finance_backend.Service;

import org.springframework.stereotype.Service;

import com.example.finance.finance_backend.Model.Category;
import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.CategoryRepository;
import com.example.finance.finance_backend.Repository.UserRepository;
import com.example.finance.finance_backend.Request.CategoryRequest;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository,
            JwtService jwtService) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public Iterable<Category> getAvailableCategories(HttpServletRequest request) {

        String username = jwtService.getAuthUser(request);

        if (username == null) {
            throw new RuntimeException("User not authenticated");
        }

        User user = userRepository.findByEmail(username).orElseThrow();
        Long userId = user.getId();

        return categoryRepository.findAvailableCategories(userId);
    }

    public Category createCategory(CategoryRequest request, HttpServletRequest httpRequest) {

        String email = jwtService.getAuthUser(httpRequest);

        if (email == null) {
            throw new RuntimeException("User not authenticated");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Category category = new Category();
        category.setName(request.getName());
        category.setUser(user);

        return categoryRepository.save(category);
    }

    public Category updateCategory(Long categoryId, CategoryRequest request, HttpServletRequest httpRequest) {

        String email = jwtService.getAuthUser(httpRequest);

        if (email == null) {
            throw new RuntimeException("User not authenticated");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Category category = categoryRepository.findById(categoryId).orElseThrow();

        if (category.getUser() == null || !category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot modify this category");
        }

        category.setName(request.getName());

        return categoryRepository.save(category);

    }

    public void deleteCategory(Long categoryId, HttpServletRequest httpRequest) {

        String email = jwtService.getAuthUser(httpRequest);

        if (email == null) {
            throw new RuntimeException("User not authenticated");
        }

        User user = userRepository.findByEmail(email).orElseThrow();

        Category category = categoryRepository.findById(categoryId).orElseThrow();

        if (category.getUser() == null || !category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot delete this category");
        }

        categoryRepository.delete(category);
    }
}
