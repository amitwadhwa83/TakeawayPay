package com.takeaway.pay.exception;

public class InsufficientFundsException extends GenericException {
    public InsufficientFundsException(long accountId) {
        super(String.format("Insufficient funds in account:%s", accountId));
    }
}
