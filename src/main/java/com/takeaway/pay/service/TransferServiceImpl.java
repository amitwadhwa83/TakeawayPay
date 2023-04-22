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
import java.util.Optional;

@Service
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    @Autowired
    private final AccountService accountService;

    private static final BigDecimal DAILY_LIMIT = new BigDecimal(10);

    public TransferServiceImpl(TransferRepository transferRepository, AccountService accountService) {
        this.transferRepository = transferRepository;
        this.accountService = accountService;
    }

    @Override
    public List<Transfer> listTransfer() {
        return transferRepository.findAll();
    }

    @Override
    public long tranferToRestaurant(long customerAccount, long restaurantAccount, BigDecimal amount)
            throws InsufficientFundsException, InvalidAmountException, InvalidAccountException, DailyLimitExceededException {
        validateAmount(amount);
        validateDailyLimitForCustomerAccount(customerAccount, amount);
        Account fromAccount = validateAndGetCustomerAccount(customerAccount);
        Account toAccount = validateAndGetRestaurantAccount(restaurantAccount);
        return tranferMoney(fromAccount, toAccount, amount);
    }

    private void validateAmount(BigDecimal amount) throws InvalidAmountException, DailyLimitExceededException {
        if (null == amount || amount.signum() == -1) {
            throw new InvalidAmountException(amount);
        }
        if (amount.compareTo(DAILY_LIMIT) > 0) {
            throw new DailyLimitExceededException("Amount is greater than daily limit of :" + DAILY_LIMIT);
        }
    }

    private void validateDailyLimitForCustomerAccount(long customerAccount, BigDecimal amount) throws DailyLimitExceededException {
        List<Transfer> transfersForToday = transferRepository.findByDate(customerAccount, LocalDate.now());
        if (!transfersForToday.isEmpty()) {
            BigDecimal totalTransfersForToday = transfersForToday.stream()
                    .map(transfer -> transfer.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            boolean dailyLimitExhausted = DAILY_LIMIT.compareTo(totalTransfersForToday) <= 0;
            if (dailyLimitExhausted)
                throw new DailyLimitExceededException(customerAccount, "0");

            boolean dailyLimitWillBeExhausted = DAILY_LIMIT.subtract(amount).compareTo(totalTransfersForToday) < 0;
            if (dailyLimitWillBeExhausted)
                throw new DailyLimitExceededException(customerAccount, amount.toString());
        }
    }

    private Account validateAndGetCustomerAccount(long customerAccount) throws InvalidAccountException {
        Optional<Account> account = accountService.findById(customerAccount);
        if (!account.isPresent() || !account.get().isCustomer()) {
            throw new InvalidAccountException("Customer account not found or invalid:" + customerAccount);
        }
        return account.get();
    }

    private Account validateAndGetRestaurantAccount(long restaurantAccount) throws InvalidAccountException {
        Optional<Account> account = accountService.findById(restaurantAccount);
        if (!account.isPresent() || account.get().isCustomer()) {
            throw new InvalidAccountException("Restaurant account not found or invalid:" + restaurantAccount);
        }
        return account.get();
    }

    private static final Object lock = new Object();

    private long tranferMoney(Account fromAccount, Account toAccount, BigDecimal amount) throws InsufficientFundsException {
        class Helper {
            public long transfer() throws InsufficientFundsException {
                accountService.debitCustomerAccount(fromAccount.getId(), amount);
                accountService.creditRestaurantAccount(toAccount.getId(), amount);
                return transferRepository.save(new Transfer(fromAccount.getId(), toAccount.getId(), amount))
                        .getId();
            }
        }
        synchronized (fromAccount) {
            synchronized (toAccount) {
                return new Helper().transfer();
            }
        }
    }
}