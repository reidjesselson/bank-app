package com.ippon.bankapp.domain;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.time.LocalDate;

@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "DateTime")
    private LocalDate dateTime;

    @NotNull
    @ManyToOne
    private Account account;


    public Transaction(){}

    public Transaction(String transactionType, BigDecimal amount, Account account, LocalDate dateTime) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.account = account;
        this.dateTime = dateTime;
    }

    public String getTransactionType(){
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDate getDateTime() {return dateTime; }

    public void setLocalDate(LocalDate dateTime){this.dateTime = dateTime; }

    public Account getAccount(){
        return account;
    }

    public void setAccount(Account account){
        this.account = account;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public void setAmount(BigDecimal amount){
        this.amount = amount;
    }

    public int getId(){
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAccount());
    }
}
