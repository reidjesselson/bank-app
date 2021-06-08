package com.ippon.bankapp.service.dto;

import com.ippon.bankapp.domain.Account;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class TransactionDTO {
    @NotEmpty
    private String transactionType;

    @NotEmpty
    private Account account;

    @NotEmpty
    private BigDecimal amount;

    @NotEmpty
    private LocalDate dateTime;

    public TransactionDTO() {

    }

    public String getTransactionType() {
        return transactionType;
    }

    public TransactionDTO transactionType(String transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDate getDateTime() { return dateTime; }

    public TransactionDTO dateTime(LocalDate dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public void setDateTime(LocalDate dateTime) { this.dateTime = dateTime; }
    public Account getAccount() {
        return account;
    }

    public TransactionDTO account(Account account) {
        this.account = account;
        return this;
    }

    public void setAccount(Account account) {
        this.account = account;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionDTO amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
