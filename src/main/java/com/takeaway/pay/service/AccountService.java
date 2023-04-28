package com.takeaway.pay.service;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.exception.AccountNotExistsException;
import com.takeaway.pay.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    Account findById(long accountId) throws AccountNotExistsException;

    List<Account> listAccount();

    void doTransfer(Account fromAccount, Account toAccount, BigDecimal transferAmount) throws InsufficientFundsException,
            AccountNotExistsException;
}
