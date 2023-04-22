package com.takeaway.pay.exception;

public class DailyLimitExceededException extends GenericException {

    public DailyLimitExceededException(long customerAccount, String amount) {
        super("Daily spending limit is exhausted/will be exhausted for account:" + customerAccount + ", available amount:" + amount);
    }

    public DailyLimitExceededException(String message) {
        super(message);
    }
}
