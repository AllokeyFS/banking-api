package com.bank.bankingapi.dto;

import com.bank.bankingapi.model.Transaction;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {

    private String sourceAccountNumber;
    private String targetAccountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 15, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private Transaction.TransactionType type;

    private String description;
}