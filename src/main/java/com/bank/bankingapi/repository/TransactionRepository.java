package com.bank.bankingapi.repository;

import com.bank.bankingapi.model.Transaction;
import com.bank.bankingapi.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceAccountOrTargetAccountOrderByCreatedAtDesc(
            Account sourceAccount, Account targetAccount);
}