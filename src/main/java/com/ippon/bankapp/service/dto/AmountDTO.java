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

    public AmountDTO amount(BigDecimal amount){
        this.amount = amount;
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
