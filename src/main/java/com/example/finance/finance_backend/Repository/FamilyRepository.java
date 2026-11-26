package com.example.finance.finance_backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.finance.finance_backend.Model.Family;

public interface FamilyRepository extends JpaRepository<Family, Long> {
    Optional<Family> findByCode(String code);
}
