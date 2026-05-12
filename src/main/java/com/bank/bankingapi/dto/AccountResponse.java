package com.bank.bankingapi.dto;

import com.bank.bankingapi.model.Account;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountResponse {
    private Long id;
    private String ownerName;
    private String accountNumber;
    private BigDecimal balance;
    private Account.AccountType accountType;
    private LocalDateTime createdAt;
}