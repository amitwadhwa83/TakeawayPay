package com.takeaway.pay.exception;

public class DailyLimitExceededException extends GenericException {

    private static final String MESSAGE = "Daily spending limit is exhausted/will be exhausted for account:%s, available amount:%s";

    public DailyLimitExceededException(String message) {
        super(message);
    }

    public DailyLimitExceededException(long account, String amount) {
        super(String.format(MESSAGE, account, amount));
    }


}
