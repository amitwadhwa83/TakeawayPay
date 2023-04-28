package com.takeaway.pay.exception;

public class InvalidTransferException extends GenericException {
    public InvalidTransferException() {
        super("Transfer can only be done from a customer account to restaurant account");
    }
}
