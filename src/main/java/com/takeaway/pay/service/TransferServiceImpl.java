package com.takeaway.pay.service;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.domain.Transfer;
import com.takeaway.pay.exception.*;
import com.takeaway.pay.repository.TransferRepository;
import com.takeaway.pay.util.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

        Account customerAccount = validateAndGetAccount(CUSTOMER, transfer.getSourceAccount());
        Account restaurantAccount = validateAndGetAccount(BUSINESS, transfer.getDestAccount());
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
                throw new DailyLimitExceededException(account, debitAmount.toString());
        }
    }

    private boolean dailyLimitExhausted(BigDecimal totalTransfersForToday) {
        return DAILY_LIMIT_IN_EUR.compareTo(totalTransfersForToday) <= 0;
    }

    private boolean dailyLimitWillBeExhausted(BigDecimal totalTransfersForToday, BigDecimal debitAmount) {
        return DAILY_LIMIT_IN_EUR.subtract(debitAmount).compareTo(totalTransfersForToday) < 0;
    }


    private Account validateAndGetAccount(AccountType accountType, long account) throws InvalidAccountException {
        Optional<Account> accnt = accountService.findById(account);
        if (!accnt.isPresent() ||
                (accountType.equals(CUSTOMER) && !accnt.get().isCustomer()) ||
                (accountType.equals(BUSINESS) && accnt.get().isCustomer())) {
            throw new InvalidAccountException("Account not found or invalid:" + account);
        }
        return accnt.get();
    }

    private static final Object lock = new Object();

    @Transactional(rollbackFor = GenericException.class)
    private long tranferMoney(Account fromAccount, Account toAccount, BigDecimal amount) throws InsufficientFundsException {
        class Helper {
            public long transfer() throws InsufficientFundsException {
                accountService.debitCustomerAccount(fromAccount.getId(), amount);
                accountService.creditRestaurantAccount(toAccount.getId(), amount);
                return transferRepository.save(new Transfer(fromAccount.getId(), toAccount.getId(), amount))
                        .getId();
            }
        }
        int fromHash = System.identityHashCode(fromAccount);
        int toHash = System.identityHashCode(toAccount);
        if (fromHash < toHash) {
            synchronized (fromAccount) {
                synchronized (toAccount) {
                    return new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            synchronized (toAccount) {
                synchronized (fromAccount) {
                    return new Helper().transfer();
                }
            }
        } else {
            synchronized (lock) {
                synchronized (fromAccount) {
                    synchronized (toAccount) {
                        return new Helper().transfer();
                    }
                }
            }
        }
    }
}