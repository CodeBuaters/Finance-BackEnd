package com.example.finance.finance_backend.Controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.finance.finance_backend.Dto.FamilyResponse;
import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Repository.TransactionRepository;
import com.example.finance.finance_backend.Request.FamilyRequest;
import com.example.finance.finance_backend.Service.FamilyService;

import jakarta.validation.Valid;

@Tag(name = "Family", description = "Share the dashboard with family members who join with an invite code")
@RestController
public class FamilyRestController {

    private final FamilyService familyService;
    private final TransactionRepository transactionRepository;

    public FamilyRestController(FamilyService familyService, TransactionRepository transactionRepository) {
        this.familyService = familyService;
        this.transactionRepository = transactionRepository;
    }

    @Operation(summary = "Get my family", description = "Returns your family with its invite code and members. The body is empty when you are not in a family.")
    @GetMapping("/api/family")
    public FamilyResponse getFamily(Authentication authentication) {
        return familyService.getFamily(authentication.getName());
    }

    @Operation(summary = "List my family's transactions", description = "Returns the transactions of every family member, or only yours when you are not in a family.")
    @GetMapping("/api/family/transactions")
    public Iterable<Transaction> getFamilyTransactions(Authentication authentication) {
        return transactionRepository.findByUserIdIn(familyService.getUserIds(authentication.getName(), "family"));
    }

    @Operation(summary = "Create a family", description = "Creates a family and makes you its first member. 409 if you are already in a family.")
    @PostMapping("/api/family")
    public FamilyResponse createFamily(Authentication authentication, @Valid @RequestBody FamilyRequest request) {
        return familyService.createFamily(authentication.getName(), request.name());
    }

    @Operation(summary = "Join a family", description = "Joins the family with this invite code. 404 if the code is wrong, 409 if you are already in a family.")
    @PostMapping("/api/family/join")
    public FamilyResponse joinFamily(Authentication authentication, @RequestBody FamilyRequest request) {
        return familyService.joinFamily(authentication.getName(), request.code());
    }

    @Operation(summary = "Leave my family", description = "Removes you from your family. The family is deleted when its last member leaves.")
    @PostMapping("/api/family/leave")
    public void leaveFamily(Authentication authentication) {
        familyService.leaveFamily(authentication.getName());
    }
}
