package com.takeaway.pay.exception;

public class InsufficientFundsException extends GenericException {
    public InsufficientFundsException(long account) {
        super("Insufficient funds in account:" + account);
    }
}
