package com.takeaway.pay.exception;

import java.math.BigDecimal;

public class DailyLimitExceededException extends GenericException {

    private static final String MESSAGE = "Daily spending limit is exhausted/will be exhausted for account:%s, " +
            "available amount:%s EUR";

    public DailyLimitExceededException(String dailyLimitInEur) {
        super(String.format("Transfer amount is greater than daily limit of:%s EUR", dailyLimitInEur));
    }

    public DailyLimitExceededException(long accountId, String amount) {
        super(String.format(MESSAGE, accountId, amount));
    }
}
