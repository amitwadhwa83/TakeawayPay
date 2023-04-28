package com.takeaway.pay.exception;

public class IdenticalAccountsException extends GenericException {
    public IdenticalAccountsException(long debitAccount, long creditAccount) {
        super(String.format("Debit account:%s and credit account:%s are identical", debitAccount, creditAccount));
    }
}