package com.example.finance.finance_backend.Config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.finance.finance_backend.Service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Component 
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public AuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override 
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

            String user = jwtService.getAuthUser(request);

            if (user != null) {
                Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                        user, 
                        null,
                        List.of()
                    );

                SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        }
    
}
