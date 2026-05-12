package com.bank.bankingapi.dto;

import com.bank.bankingapi.model.Transaction;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private BigDecimal amount;
    private Transaction.TransactionType type;
    private String description;
    private LocalDateTime createdAt;
}