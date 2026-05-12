package com.bank.bankingapi.dto;

import com.bank.bankingapi.model.Transaction;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private BigDecimal amount;
    private Transaction.TransactionType type;
    private String description;
}