package com.takeaway.pay.exception;

public class AccountNotExistsException extends GenericException {
    public AccountNotExistsException(long accountId) {
        super(String.format("Account id:%s does not exists", accountId));
    }
}
