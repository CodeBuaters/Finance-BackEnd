package com.example.finance.finance_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.UserRepository;

@SpringBootApplication
public class FinanceBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner createAdmin(UserRepository repository, BCryptPasswordEncoder encoder) {
		return args -> repository.findByUsername("admin").orElseGet(() -> repository.save(
				new User("admin", "Admin", "User", "admin@finance.local", encoder.encode("admin123"))));
	}

}
