package com.takeaway.pay.exception;

import java.math.BigDecimal;

public class InvalidAmountException extends GenericException {
    public InvalidAmountException(BigDecimal transferAmount) {
        super(String.format("Amount:%s is not valid for transfer", transferAmount));
    }
}
