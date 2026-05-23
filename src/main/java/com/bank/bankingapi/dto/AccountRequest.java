package com.bank.bankingapi.dto;

import com.bank.bankingapi.model.Account;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountRequest {

    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;

    @DecimalMin(value = "0.0", inclusive = true, message = "Initial balance cannot be negative")
    private BigDecimal initialBalance;
}