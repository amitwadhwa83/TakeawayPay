package com.takeaway.pay.service;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.exception.AccountNotExistsException;
import com.takeaway.pay.exception.InsufficientFundsException;
import com.takeaway.pay.util.AccountType;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    List<Account> listAccount();

    void doTransfer(Account fromAccount, Account toAccount, BigDecimal transferAmount) throws InsufficientFundsException,
            AccountNotExistsException;

    Account validateAndGetAccount(long accountId, AccountType accountType) throws AccountNotExistsException;
}
