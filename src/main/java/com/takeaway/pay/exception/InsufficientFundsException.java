package com.takeaway.pay.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(long accountId) {
        super(String.format("Insufficient funds in account:%s", accountId));
    }
}
