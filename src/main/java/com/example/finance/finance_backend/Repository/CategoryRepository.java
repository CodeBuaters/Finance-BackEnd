package com.example.finance.finance_backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.finance.finance_backend.Model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("""
            SELECT c FROM Category c
            WHERE c.user IS NULL OR c.user.id = :userId
            """)
    Iterable<Category> findAvailableCategories(@Param("userId") Long userId);

    Optional<Category> findByNameAndUserIsNull(String name);

}
