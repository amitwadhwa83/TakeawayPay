package com.takeaway.pay.service;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.exception.InsufficientFundsException;
import com.takeaway.pay.util.AccountType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<Account> listAccount();

    Optional<Account> findById(long accountId);

    void doTransfer(Account fromAccount, Account toAccount, BigDecimal amount) throws InsufficientFundsException;

    Account validateAndGetAccount(AccountType accountType, long account);
}
