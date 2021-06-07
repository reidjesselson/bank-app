package com.ippon.bankapp.service.dto;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Objects;

public class AmountDTO {

    private BigDecimal amount;

    public AmountDTO() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO)==1) {
            this.amount = amount;
        }
        else{
            throw new IllegalArgumentException("amount must be greater than 0");
        }
    }
}
