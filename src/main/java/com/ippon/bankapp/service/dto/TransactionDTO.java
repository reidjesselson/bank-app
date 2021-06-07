package com.ippon.bankapp.service.dto;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Objects;

public class TransactionDTO {
    @NotEmpty
    private String transactionType;

    @NotEmpty
    private String senderFirstName;

    @NotEmpty
    private String senderLastName;

    @NotEmpty
    private BigDecimal amount;

    private String receiverFirstName;

    private String receiverLastName;

    public TransactionDTO(){

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
    public String getSenderFirstName() {
        return senderFirstName;
    }

    public TransactionDTO senderFirstName(String senderFirstName) {
        this.senderFirstName = senderFirstName;
        return this;
    }

    public void setSenderFirstName(String senderFirstName) {
        this.senderFirstName = senderFirstName;
    }

    public String getSenderLastName() {
        return senderLastName;
    }

    public TransactionDTO senderLastName(String senderLastName) {
        this.senderLastName = senderLastName;
        return this;
    }

    public void setSenderLastName(String senderLastName) {
        this.senderLastName = senderLastName;
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

    public String getReceiverFirstName() {
        return receiverFirstName;
    }

    public TransactionDTO receiverFirstName(String receiverFirstName) {
        this.receiverFirstName = receiverFirstName;
        return this;
    }

    public void setReceiverFirstName(String receiverFirstName) {
        this.receiverFirstName = receiverFirstName;
    }

    public String getReceiverLastName() {
        return receiverLastName;
    }

    public TransactionDTO receiverLastName(String receiverLastName) {
        this.receiverLastName = receiverLastName;
        return this;
    }

    public void setReceiverLastName(String receiverLastName) {
        this.receiverLastName = receiverLastName;
    }
}
