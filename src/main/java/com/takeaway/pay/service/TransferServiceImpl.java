package com.takeaway.pay.service;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.domain.Transfer;
import com.takeaway.pay.exception.DailyLimitExceededException;
import com.takeaway.pay.exception.InsufficientFundsException;
import com.takeaway.pay.exception.InvalidAccountException;
import com.takeaway.pay.exception.InvalidAmountException;
import com.takeaway.pay.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.takeaway.pay.util.AccountType.BUSINESS;
import static com.takeaway.pay.util.AccountType.CUSTOMER;

@Service
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    @Autowired
    private final AccountService accountService;

    private static final BigDecimal DAILY_LIMIT_IN_EUR = new BigDecimal(10);

    public TransferServiceImpl(TransferRepository transferRepository, AccountService accountService) {
        this.transferRepository = transferRepository;
        this.accountService = accountService;
    }

    @Override
    public List<Transfer> listTransfer() {
        return transferRepository.findAll();
    }

    @Override
    public long doTranfer(Transfer transfer)
            throws InsufficientFundsException, InvalidAmountException, InvalidAccountException, DailyLimitExceededException {
        Account customerAccount = accountService.validateAndGetAccount(CUSTOMER, transfer.getSourceAccount());
        Account restaurantAccount = accountService.validateAndGetAccount(BUSINESS, transfer.getDestAccount());
        BigDecimal transferAmount = transfer.getAmount();
        validateAmount(transferAmount);
        validateDailyLimitForCustomerAccount(transfer.getSourceAccount(), transferAmount);
        return tranferMoney(customerAccount, restaurantAccount, transferAmount);
    }

    private void validateAmount(BigDecimal amount) throws InvalidAmountException {
        if (null == amount || amount.signum() <= 0) {
            throw new InvalidAmountException(amount);
        }
    }

    private void validateDailyLimitForCustomerAccount(long account, BigDecimal debitAmount) throws DailyLimitExceededException {
        if (debitAmount.compareTo(DAILY_LIMIT_IN_EUR) > 0) {
            throw new DailyLimitExceededException("Amount is greater than daily limit of :" + DAILY_LIMIT_IN_EUR);
        }
        List<Transfer> transfersForToday = transferRepository.findByDate(account, LocalDate.now());
        if (!transfersForToday.isEmpty()) {
            BigDecimal totalTransfersForToday = transfersForToday.stream()
                    .map(transfer -> transfer.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (dailyLimitExhausted(totalTransfersForToday))
                throw new DailyLimitExceededException(account, "0");

            if (dailyLimitWillBeExhausted(totalTransfersForToday, debitAmount))
                throw new DailyLimitExceededException(account, DAILY_LIMIT_IN_EUR.subtract(totalTransfersForToday).toString());
        }
    }

    private boolean dailyLimitExhausted(BigDecimal totalTransfersForToday) {
        return DAILY_LIMIT_IN_EUR.compareTo(totalTransfersForToday) <= 0;
    }

    private boolean dailyLimitWillBeExhausted(BigDecimal totalTransfersForToday, BigDecimal debitAmount) {
        return DAILY_LIMIT_IN_EUR.subtract(debitAmount).compareTo(totalTransfersForToday) < 0;
    }

    public long tranferMoney(Account fromAccount, Account toAccount, BigDecimal amount) throws InsufficientFundsException {
        accountService.doTransfer(fromAccount, toAccount, amount);
        return transferRepository.save(new Transfer(fromAccount.getId(), toAccount.getId(), amount))
                .getId();
    }
}