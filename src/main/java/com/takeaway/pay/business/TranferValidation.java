package com.takeaway.pay.business;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.domain.Transfer;
import com.takeaway.pay.exception.DailyLimitExceededException;
import com.takeaway.pay.exception.IdenticalAccountsException;
import com.takeaway.pay.exception.InvalidAmountException;
import com.takeaway.pay.exception.InvalidTransferException;

import java.math.BigDecimal;
import java.util.List;

public enum TranferValidation {

    INSTANCE;

    private static final BigDecimal DAILY_LIMIT_IN_EUR = new BigDecimal(10);

    public static void validateTransfer(Account sourceAccount, Account destAccount, BigDecimal transferAmount,
                                        List<Transfer> transfersForToday) throws DailyLimitExceededException,
            IdenticalAccountsException, InvalidAmountException, InvalidTransferException {

        long debitAccount = sourceAccount.getId();
        long creditAccount = destAccount.getId();

        if (debitAccount == creditAccount) {
            throw new IdenticalAccountsException(debitAccount, creditAccount);
        }
        if (sourceAccount.isNotCustomerAccount() || destAccount.isCustomerAccount()) {
            throw new InvalidTransferException();
        }
        if (null == transferAmount || transferAmount.signum() <= 0) {
            throw new InvalidAmountException(transferAmount);
        }
        validateDailyLimit(debitAccount, transferAmount, transfersForToday);
    }

    private static void validateDailyLimit(long accountId, BigDecimal debitAmount,
                                           List<Transfer> transfersForToday) throws DailyLimitExceededException {
        if (debitAmount.compareTo(DAILY_LIMIT_IN_EUR) > 0) {
            throw new DailyLimitExceededException(DAILY_LIMIT_IN_EUR.toString());
        }
        if (!transfersForToday.isEmpty()) {
            BigDecimal totalTransfersForToday = transfersForToday.stream()
                    .map(transfer -> transfer.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            boolean dailyLimitExhausted = DAILY_LIMIT_IN_EUR.compareTo(totalTransfersForToday) <= 0;
            if (dailyLimitExhausted)
                throw new DailyLimitExceededException(accountId, "0");

            BigDecimal availableAmountForDay = DAILY_LIMIT_IN_EUR.subtract(totalTransfersForToday);
            boolean dailyLimitWillBeExhausted = DAILY_LIMIT_IN_EUR.subtract(debitAmount).compareTo(totalTransfersForToday) < 0;
            if (dailyLimitWillBeExhausted)
                throw new DailyLimitExceededException(accountId, availableAmountForDay.toString());
        }
    }
}