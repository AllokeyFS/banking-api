package com.bank.bankingapi.dto;

import com.bank.bankingapi.model.Account;
import lombok.Data;

@Data
public class AccountRequest {
    private String ownerName;
    private Account.AccountType accountType;
    private java.math.BigDecimal initialBalance;
}