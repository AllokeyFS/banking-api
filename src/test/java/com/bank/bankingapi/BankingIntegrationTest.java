package com.bank.bankingapi;

import com.bank.bankingapi.dto.*;
import com.bank.bankingapi.model.Account;
import com.bank.bankingapi.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BankingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String accountNumber;

    @BeforeEach
    void setUp() throws Exception {
        // Создаём аккаунт перед каждым тестом
        AccountRequest request = new AccountRequest();
        request.setOwnerName("John Doe");
        request.setAccountType(Account.AccountType.CHECKING);
        request.setInitialBalance(new BigDecimal("1000.00"));

        MvcResult result = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        AccountResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccountResponse.class);
        accountNumber = response.getAccountNumber();
    }

    @Test
    void createAccount_success() throws Exception {
        AccountRequest request = new AccountRequest();
        request.setOwnerName("Jane Doe");
        request.setAccountType(Account.AccountType.SAVINGS);
        request.setInitialBalance(new BigDecimal("500.00"));

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerName").value("Jane Doe"))
                .andExpect(jsonPath("$.balance").value(500.00))
                .andExpect(jsonPath("$.accountNumber").isNotEmpty());
    }

    @Test
    void createAccount_missingOwnerName_returns400() throws Exception {
        AccountRequest request = new AccountRequest();
        request.setAccountType(Account.AccountType.CHECKING);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void deposit_success() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setTargetAccountNumber(accountNumber);
        request.setAmount(new BigDecimal("200.00"));
        request.setType(Transaction.TransactionType.DEPOSIT);

        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(200.00));
    }

    @Test
    void withdraw_insufficientFunds_returns422() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setSourceAccountNumber(accountNumber);
        request.setAmount(new BigDecimal("9999.00"));
        request.setType(Transaction.TransactionType.WITHDRAWAL);

        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_FUNDS"));
    }

    @Test
    void withdraw_negativeAmount_returns400() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setSourceAccountNumber(accountNumber);
        request.setAmount(new BigDecimal("-50.00"));
        request.setType(Transaction.TransactionType.WITHDRAWAL);

        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void transfer_success() throws Exception {
        // Создаём второй аккаунт
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setOwnerName("Target User");
        accountRequest.setAccountType(Account.AccountType.CHECKING);
        accountRequest.setInitialBalance(new BigDecimal("0.00"));

        MvcResult result = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andReturn();

        AccountResponse target = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccountResponse.class);

        TransactionRequest request = new TransactionRequest();
        request.setSourceAccountNumber(accountNumber);
        request.setTargetAccountNumber(target.getAccountNumber());
        request.setAmount(new BigDecimal("100.00"));
        request.setType(Transaction.TransactionType.TRANSFER);

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("TRANSFER"))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void getHistory_returnsPaginatedResults() throws Exception {
        mockMvc.perform(get("/api/accounts/" + accountNumber + "/history")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAccount_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/accounts/ACC-NOTEXIST"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
    }
}