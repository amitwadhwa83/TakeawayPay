package com.takeaway.pay.service.impl;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.exception.AccountNotExistsException;
import com.takeaway.pay.exception.InsufficientFundsException;
import com.takeaway.pay.repository.AccountRepository;
import com.takeaway.pay.service.AccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account findById(long accountId) throws AccountNotExistsException {
        return accountRepository.findById(accountId).orElseThrow(() -> new AccountNotExistsException(accountId));
    }

    @Override
    public List<Account> listAccount() {
        return accountRepository.findAll();
    }

    private void creditAccount(Account account, BigDecimal transferAmount) {
        account.setBalance(account.getBalance().add(transferAmount));
        account.setLastUpdate(LocalDateTime.now());
        accountRepository.save(account);
    }

    private void debitAccount(Account account, BigDecimal transferAmount) throws InsufficientFundsException {
        if (account.getBalance().compareTo(transferAmount) <= 0) {
            throw new InsufficientFundsException(account.getId());
        }
        account.setBalance(account.getBalance().subtract(transferAmount));
        account.setLastUpdate(LocalDateTime.now());
        accountRepository.save(account);
    }

    private static final Object lock = new Object();

    @Override
    public void doTransfer(Account fromAccount, Account toAccount, BigDecimal transferAmount) throws InsufficientFundsException,
            AccountNotExistsException {
        class Helper {
            public void transfer() throws InsufficientFundsException, AccountNotExistsException {
                debitAccount(fromAccount, transferAmount);
                creditAccount(toAccount, transferAmount);
            }
        }
        int fromHash = System.identityHashCode(fromAccount);
        int toHash = System.identityHashCode(toAccount);
        if (fromHash < toHash) {
            synchronized (fromAccount) {
                synchronized (toAccount) {
                    new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            synchronized (toAccount) {
                synchronized (fromAccount) {
                    new Helper().transfer();
                }
            }
        } else {
            synchronized (lock) {
                synchronized (fromAccount) {
                    synchronized (toAccount) {
                        new Helper().transfer();
                    }
                }
            }
        }
    }
}