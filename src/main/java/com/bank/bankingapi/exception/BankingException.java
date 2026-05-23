package com.bank.bankingapi.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class BankingException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public BankingException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    // Фабричные методы для частых ошибок
    public static BankingException accountNotFound(String accountNumber) {
        return new BankingException(
                "Account not found: " + accountNumber,
                HttpStatus.NOT_FOUND,
                "ACCOUNT_NOT_FOUND"
        );
    }

    public static BankingException insufficientFunds(String accountNumber) {
        return new BankingException(
                "Insufficient funds in account: " + accountNumber,
                HttpStatus.UNPROCESSABLE_ENTITY,
                "INSUFFICIENT_FUNDS"
        );
    }

    public static BankingException invalidAmount() {
        return new BankingException(
                "Amount must be greater than zero",
                HttpStatus.BAD_REQUEST,
                "INVALID_AMOUNT"
        );
    }

    public static BankingException sameAccountTransfer() {
        return new BankingException(
                "Cannot transfer to the same account",
                HttpStatus.BAD_REQUEST,
                "SAME_ACCOUNT_TRANSFER"
        );
    }
}