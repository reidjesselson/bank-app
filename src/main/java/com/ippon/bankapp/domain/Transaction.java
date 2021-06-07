package com.ippon.bankapp.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

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

    @Column(name = "sender_first_name")
    private String senderFirstName;

    @Column(name = "sender_last_name")
    private String senderLastName;

    @Column(name = "receiver_first_name")
    private String receiverFirstName;

    @Column(name = "receiver_last_name")
    private String receiverLastName;

    public Transaction(String transactionType, BigDecimal amount, String senderFirstName, String senderLastName, String receiverFirstName, String receiverLastName) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.senderFirstName = senderFirstName;
        this.senderLastName = senderLastName;
        this.receiverFirstName = receiverFirstName;
        this.receiverLastName = receiverLastName;
    }

    public String getTransactionType(){
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getSenderFirstName() {
        return senderFirstName;
    }

    public void setSenderFirstName(String senderFirstName) {
        this.senderFirstName = senderFirstName;
    }

    public String getSenderLastName(){
        return senderLastName;
    }

    public void setSenderLastName(String senderLastName){
        this.senderLastName = senderLastName;
    }

    public String getReceiverFirstName(){
        return receiverFirstName;
    }

    public void setReceiverFirstName(String receiverFirstName){
        this.receiverFirstName = receiverFirstName;
    }

    public String getReceiverLastName(){
        return receiverLastName;
    }

    public void setReceiverLastName(String receiverLastName){
        this.receiverLastName = receiverLastName;
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
        return Objects.hash(getId(), getSenderFirstName(), getSenderLastName());
    }
}
