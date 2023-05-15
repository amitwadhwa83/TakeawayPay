package com.takeaway.pay.entity;

import lombok.Getter;

import java.math.BigDecimal;

public class TopUp {

    @Getter
    private final long customerId;
    @Getter
    private final BigDecimal topUpAmount;

    public TopUp(long customerId, BigDecimal topUpAmount) {
        this.customerId = customerId;
        this.topUpAmount = topUpAmount;
    }
}