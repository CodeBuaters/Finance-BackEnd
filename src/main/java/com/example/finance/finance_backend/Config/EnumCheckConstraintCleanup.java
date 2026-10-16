package com.example.finance.finance_backend.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

// Hibernate creates a CHECK constraint for every enum column, containing only the enum values that
// existed when the table was created. ddl-auto=update never updates it, so values added later
// (e.g. TransactionType.TRANSFER) are rejected by the database with a 500 error.
// The Java enums already allow only valid values, so these constraints are dropped on every startup.
@Component
public class EnumCheckConstraintCleanup implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public EnumCheckConstraintCleanup(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        dropConstraint("transaction", "transaction_transaction_type_check");
        dropConstraint("transaction", "transaction_category_check");
        dropConstraint("budget", "budget_category_check");
    }

    private void dropConstraint(String table, String constraint) {
        jdbcTemplate.execute("ALTER TABLE IF EXISTS " + table + " DROP CONSTRAINT IF EXISTS " + constraint);
    }
}
