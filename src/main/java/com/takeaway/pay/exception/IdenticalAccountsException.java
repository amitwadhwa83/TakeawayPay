package com.takeaway.pay.exception;

public class IdenticalAccountsException extends GenericException {
    public IdenticalAccountsException(long debitAccount, long creditAccount) {
        super(String.format("Account from:%s and to:%s  are identical", debitAccount, creditAccount));
    }
}