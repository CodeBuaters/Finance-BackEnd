package com.example.finance.finance_backend.Controller.api;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.finance.finance_backend.Model.Category;
import com.example.finance.finance_backend.Request.CategoryRequest;
import com.example.finance.finance_backend.Service.CategoryService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class CategoryRestController {

    private final CategoryService categoryService;

    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/categories")
    public Iterable<Category> getAvailableCategories(HttpServletRequest request) {
        return categoryService.getAvailableCategories(request);
    }

    @PostMapping("/api/categories")
    public Category creaCategory(@RequestBody CategoryRequest request, HttpServletRequest httpRequest) {
        return categoryService.createCategory(request, httpRequest);
    }

    @PutMapping("/api/categories/{categoryId}")
    public Category updateCategory(@PathVariable Long categoryId, @RequestBody CategoryRequest request,
            HttpServletRequest httpRequest) {
        return categoryService.updateCategory(categoryId, request, httpRequest);
    }

    @DeleteMapping("/api/categories/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId, HttpServletRequest httpRequest) {
        categoryService.deleteCategory(categoryId, httpRequest);
    }

}
