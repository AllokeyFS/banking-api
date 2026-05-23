package com.bank.bankingapi.service;

import com.bank.bankingapi.dto.*;
import com.bank.bankingapi.model.Account;
import com.bank.bankingapi.model.Transaction;
import com.bank.bankingapi.repository.AccountRepository;
import com.bank.bankingapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bank.bankingapi.exception.BankingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankingService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    // ── ACCOUNT ──────────────────────────────────────────

    public AccountResponse createAccount(AccountRequest request) {
        Account account = new Account();
        account.setOwnerName(request.getOwnerName());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialBalance() != null
                ? request.getInitialBalance()
                : BigDecimal.ZERO);
        account.setAccountNumber(generateAccountNumber());
        return mapToAccountResponse(accountRepository.save(account));
    }

    public AccountResponse getAccount(String accountNumber) {
        Account account = findByNumber(accountNumber);
        return mapToAccountResponse(account);
    }

    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    // ── TRANSACTIONS ──────────────────────────────────────

    @Transactional
    public TransactionResponse deposit(TransactionRequest request) {
        Account target = findByNumber(request.getTargetAccountNumber());
        target.setBalance(target.getBalance().add(request.getAmount()));
        accountRepository.save(target);

        Transaction tx = new Transaction();
        tx.setTargetAccount(target);
        tx.setAmount(request.getAmount());
        tx.setType(Transaction.TransactionType.DEPOSIT);
        tx.setDescription(request.getDescription());
        return mapToTransactionResponse(transactionRepository.save(tx));
    }

    @Transactional
    public TransactionResponse withdraw(TransactionRequest request) {
        Account source = findByNumber(request.getSourceAccountNumber());

        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            throw BankingException.insufficientFunds(source.getAccountNumber());
        }

        source.setBalance(source.getBalance().subtract(request.getAmount()));
        accountRepository.save(source);

        Transaction tx = new Transaction();
        tx.setSourceAccount(source);
        tx.setAmount(request.getAmount());
        tx.setType(Transaction.TransactionType.WITHDRAWAL);
        tx.setDescription(request.getDescription());
        return mapToTransactionResponse(transactionRepository.save(tx));
    }

    @Transactional
    public TransactionResponse transfer(TransactionRequest request) {
        Account source = findByNumber(request.getSourceAccountNumber());
        Account target = findByNumber(request.getTargetAccountNumber());

        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            throw BankingException.insufficientFunds(source.getAccountNumber());
        }

        source.setBalance(source.getBalance().subtract(request.getAmount()));
        target.setBalance(target.getBalance().add(request.getAmount()));
        accountRepository.save(source);
        accountRepository.save(target);

        Transaction tx = new Transaction();
        tx.setSourceAccount(source);
        tx.setTargetAccount(target);
        tx.setAmount(request.getAmount());
        tx.setType(Transaction.TransactionType.TRANSFER);
        tx.setDescription(request.getDescription());
        return mapToTransactionResponse(transactionRepository.save(tx));
    }

    public PageResponse<TransactionResponse> getHistory(String accountNumber, int page, int size) {
        Account account = findByNumber(accountNumber);

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository
                .findBySourceAccountOrTargetAccountOrderByCreatedAtDesc(
                        account, account, pageable);

        PageResponse<TransactionResponse> response = new PageResponse<>();
        response.setContent(transactions.getContent()
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList()));
        response.setPage(transactions.getNumber());
        response.setSize(transactions.getSize());
        response.setTotalElements(transactions.getTotalElements());
        response.setTotalPages(transactions.getTotalPages());
        response.setLast(transactions.isLast());
        return response;
    }

    // ── HELPERS ───────────────────────────────────────────

    private Account findByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> BankingException.accountNotFound(accountNumber));
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }

    private AccountResponse mapToAccountResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setOwnerName(account.getOwnerName());
        response.setAccountNumber(account.getAccountNumber());
        response.setBalance(account.getBalance());
        response.setAccountType(account.getAccountType());
        response.setCreatedAt(account.getCreatedAt());
        return response;
    }

    private TransactionResponse mapToTransactionResponse(Transaction tx) {
        TransactionResponse response = new TransactionResponse();
        response.setId(tx.getId());
        response.setAmount(tx.getAmount());
        response.setType(tx.getType());
        response.setDescription(tx.getDescription());
        response.setCreatedAt(tx.getCreatedAt());
        if (tx.getSourceAccount() != null)
            response.setSourceAccountNumber(tx.getSourceAccount().getAccountNumber());
        if (tx.getTargetAccount() != null)
            response.setTargetAccountNumber(tx.getTargetAccount().getAccountNumber());
        return response;
    }
}