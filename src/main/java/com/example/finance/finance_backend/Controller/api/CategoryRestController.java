package com.example.finance.finance_backend.Controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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

import com.example.finance.finance_backend.Dto.CategoryResponse;

@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Categories", description = "Transaction categories: shared default categories plus the user's own custom ones")
@RestController
public class CategoryRestController {

    private final CategoryService categoryService;

    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "List available categories", description = "Returns the default categories that every user has, plus the custom categories created by the logged in user. The `default` flag tells them apart.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of categories"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @GetMapping("/api/categories")
    public Iterable<CategoryResponse> getAvailableCategories(HttpServletRequest request) {
        return categoryService.getAvailableCategories(request);
    }

    @Operation(summary = "Create a custom category", description = "Creates a new category owned by the logged in user. It can then be used for budgets and transactions.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category created"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @PostMapping("/api/categories")
    public Category createCategory(@RequestBody CategoryRequest request, HttpServletRequest httpRequest) {
        return categoryService.createCategory(request, httpRequest);
    }

    @Operation(summary = "Rename a custom category", description = "Changes the name of a category owned by the logged in user. Default categories cannot be modified.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "500", description = "Category is a default category or owned by another user", content = @Content)
    })
    @PutMapping("/api/categories/{categoryId}")
    public Category updateCategory(
            @Parameter(description = "ID of the category to rename", example = "12") @PathVariable Long categoryId, @RequestBody CategoryRequest request,
            HttpServletRequest httpRequest) {
        return categoryService.updateCategory(categoryId, request, httpRequest);
    }

    @Operation(summary = "Delete a custom category", description = "Deletes a category owned by the logged in user. Default categories cannot be deleted.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category deleted", content = @Content),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "500", description = "Category is a default category or owned by another user", content = @Content)
    })
    @DeleteMapping("/api/categories/{categoryId}")
    public void deleteCategory(
            @Parameter(description = "ID of the category to delete", example = "12") @PathVariable Long categoryId, HttpServletRequest httpRequest) {
        categoryService.deleteCategory(categoryId, httpRequest);
    }

}
