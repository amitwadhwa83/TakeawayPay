package com.takeaway.pay.service;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.exception.InsufficientFundsException;
import com.takeaway.pay.exception.InvalidAccountException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<Account> listAccount();

    Optional<Account> findById(long accountId);

    void creditRestaurantAccount(long accountId, BigDecimal amount);

    void debitCustomerAccount(long accountId, BigDecimal amount) throws InsufficientFundsException;
}
