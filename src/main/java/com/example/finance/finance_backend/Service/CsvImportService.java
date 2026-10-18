package com.example.finance.finance_backend.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.finance.finance_backend.Model.Category;
import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;
import com.example.finance.finance_backend.Repository.CategoryRepository;

@Service
public class CsvImportService {

    private final RuleBasedCategorizationService categorizationService;
    private final CategoryRepository categoryRepository;

    public CsvImportService(
            RuleBasedCategorizationService categorizationService,
            CategoryRepository categoryRepository) {
        this.categorizationService = categorizationService;
        this.categoryRepository = categoryRepository;
    }

    public Iterable<Transaction> parseCsv(MultipartFile file) {
        if (file == null || file.isEmpty() || !file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("The CSV file must not be empty and must have a .csv extension");
        }

        List<Transaction> transactions = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim()
                        .parse(reader)) {
            Map<String, String> headers = new HashMap<>();
            for (String header : parser.getHeaderNames()) {
                headers.put(normalize(header), header);
            }

            requireHeader(headers, "amount");
            requireHeader(headers, "merchant");

            for (CSVRecord record : parser) {
                if (record.size() == 0 || record.toString().trim().isEmpty()) {
                    continue;
                }

                Transaction transaction = new Transaction();
                String rawAmount = required(record, headers, "amount");
                BigDecimal amount;
                try {
                    amount = new BigDecimal(rawAmount);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException("Invalid amount: " + rawAmount, exception);
                }
                transaction.setAmount(amount);
                transaction.setMerchant(required(record, headers, "merchant"));
                transaction.setDescription(optional(record, headers, "description"));
                transaction.setTransactionDate(parseDate(record, headers));
                transaction.setTransactionType(parseTransactionType(record, headers, amount));
                transaction.setCategory(parseCategory(
                        optional(record, headers, "category"),
                        transaction.getMerchant()));
                transactions.add(transaction);
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not read CSV file", exception);
        }
        return transactions;
    }

    private TransactionType parseTransactionType(CSVRecord record, Map<String, String> headers, BigDecimal amount) {
        String type = optional(record, headers, "transactiontype", "type");
        if (type == null) {
            return amount.compareTo(BigDecimal.ZERO) >= 0 ? TransactionType.INCOME : TransactionType.EXPENSE;
        }

        try {
            return TransactionType.valueOf(type.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid transaction type: " + type
                    + ". Expected INCOME, EXPENSE, or TRANSFER", exception);
        }
    }

    // Uses the CSV category when it is recognised, otherwise categorizes by
    // merchant
    private Category parseCategory(String category, String merchant) {
        if (category != null) {
            Optional<Category> matchedCategory = categoryRepository
                    .findByNameAndUserIsNull(category);

            if (matchedCategory.isPresent()) {
                return matchedCategory.get();
            }
        }

        return categorizationService.categorize(merchant);
    }

    private LocalDate parseDate(CSVRecord record, Map<String, String> headers) {
        String date = optional(record, headers, "transactiondate", "date");
        if (date == null) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid date: " + date + ". Expected format YYYY-MM-DD", exception);
        }
    }

    private String required(CSVRecord record, Map<String, String> headers, String... names) {
        String value = optional(record, headers, names);
        if (value == null) {
            throw new IllegalArgumentException("Missing required CSV value: " + names[0]);
        }
        return value;
    }

    private String optional(CSVRecord record, Map<String, String> headers, String... names) {
        for (String name : names) {
            String header = headers.get(normalize(name));
            if (header != null && record.isMapped(header)) {
                String value = record.get(header);
                return value.isBlank() ? null : value;
            }
        }
        return null;
    }

    private void requireHeader(Map<String, String> headers, String... names) {
        for (String name : names) {
            if (headers.containsKey(normalize(name))) {
                return;
            }
        }
        throw new IllegalArgumentException("Missing required CSV header: " + names[0]);
    }

    private String normalize(String value) {
        return value.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }
}
