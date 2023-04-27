package com.takeaway.pay.util.business;

import com.takeaway.pay.domain.Transfer;
import com.takeaway.pay.exception.DailyLimitExceededException;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public enum TranferValidators {

    INSTANCE;

    private static final BigDecimal DAILY_LIMIT_IN_EUR = new BigDecimal(10);

    public static void validateTransaction(long debitAccount, long creditAccount, BigDecimal transferAmount,
                                           List<Transfer> transfersForToday) throws DailyLimitExceededException {
        validateAccountIsDifferent(debitAccount, creditAccount);
        validateAmountIsNonZero(transferAmount);
        validateDailyLimitNotBreachedForAccount(debitAccount, transferAmount, transfersForToday);
    }

    private static void validateAccountIsDifferent(long debitAccount, long creditAccount) {
        if (debitAccount == creditAccount) {
            throw new IllegalArgumentException("Accounts must be different");
        }
    }

    private static void validateAmountIsNonZero(BigDecimal amount) {
        if (null == amount || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    private static void validateDailyLimitNotBreachedForAccount(long account, BigDecimal debitAmount,
                                                                List<Transfer> transfersForToday) throws DailyLimitExceededException {
        if (debitAmount.compareTo(DAILY_LIMIT_IN_EUR) > 0) {
            throw new DailyLimitExceededException("Amount is greater than daily limit of :" + DAILY_LIMIT_IN_EUR);
        }
        if (!transfersForToday.isEmpty()) {
            BigDecimal totalTransfersForToday = transfersForToday.stream()
                    .map(transfer -> transfer.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (dailyLimitExhausted(totalTransfersForToday))
                throw new DailyLimitExceededException(account, "0");

            BigDecimal availableAmountForDay = DAILY_LIMIT_IN_EUR.subtract(totalTransfersForToday);
            if (dailyLimitWillBeExhausted(totalTransfersForToday, debitAmount))
                throw new DailyLimitExceededException(account, availableAmountForDay.toString());
        }
    }

    private static boolean dailyLimitExhausted(BigDecimal totalTransfersForToday) {
        return DAILY_LIMIT_IN_EUR.compareTo(totalTransfersForToday) <= 0;
    }

    private static boolean dailyLimitWillBeExhausted(BigDecimal totalTransfersForToday, BigDecimal debitAmount) {
        return DAILY_LIMIT_IN_EUR.subtract(debitAmount).compareTo(totalTransfersForToday) < 0;
    }

    public static boolean validateCcyCode(String inputCcyCode) {
        try {
            Currency instance = Currency.getInstance(inputCcyCode);
            return instance.getCurrencyCode().equals(inputCcyCode);
        } catch (Exception e) {
        }
        return false;
    }
}