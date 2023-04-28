package com.takeaway.pay.exception;

import com.takeaway.pay.util.AccountType;

public class AccountNotExistsException extends GenericException {
    public AccountNotExistsException(long accountId) {
        super(String.format("Account id:%s does not exists", accountId));
    }

    public AccountNotExistsException(long accountId, AccountType accountType) {
        super(String.format("Account:%s not found or invalid for type:%s", accountId, accountType));
    }
}
