package com.bank.bankingapi.repository;

import com.bank.bankingapi.model.Transaction;
import com.bank.bankingapi.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findBySourceAccountOrTargetAccountOrderByCreatedAtDesc(
            Account sourceAccount, Account targetAccount, Pageable pageable);
}