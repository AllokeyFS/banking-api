package com.bank.bankingapi.controller;

import com.bank.bankingapi.dto.*;
import com.bank.bankingapi.service.BankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import com.bank.bankingapi.dto.PageResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BankingController {

    private final BankingService bankingService;

    // ── ACCOUNTS ──────────────────────────────────────────

    @PostMapping("/accounts")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        return ResponseEntity.ok(bankingService.createAccount(request));
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(bankingService.getAllAccounts());
    }

    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(bankingService.getAccount(accountNumber));
    }

    @GetMapping("/accounts/{accountNumber}/history")
    public ResponseEntity<PageResponse<TransactionResponse>> getHistory(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bankingService.getHistory(accountNumber, page, size));
    }

    // ── TRANSACTIONS ──────────────────────────────────────
    @PostMapping("/transactions/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(bankingService.deposit(request));
    }

    @PostMapping("/transactions/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(bankingService.withdraw(request));
    }

    @PostMapping("/transactions/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(bankingService.transfer(request));
    }
}