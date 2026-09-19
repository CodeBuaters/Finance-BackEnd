package com.example.finance.finance_backend.Controller.api;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.finance.finance_backend.Dto.SummaryDto;
import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.UserRepository;
import com.example.finance.finance_backend.Service.SummaryService;

@RestController
public class DashboardRestController {

    private final SummaryService summaryService;
    private final UserRepository userRepository;

    public DashboardRestController(SummaryService summaryService, UserRepository userRepository) {
        this.summaryService = summaryService;
        this.userRepository = userRepository;

    }

    @GetMapping("/api/dashboard/summary")
    public SummaryDto getSummary(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();

        Long userId = user.getId();

        return summaryService.getSummary(userId);
    }
}
