package com.takeaway.pay.service;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.exception.AccountNotExistsException;
import com.takeaway.pay.exception.InsufficientFundsException;
import com.takeaway.pay.repository.AccountRepository;
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

    private void creditAccount(long accountId, BigDecimal transferAmount) throws AccountNotExistsException {
        accountRepository.findById(accountId).map(acct -> {
            acct.setBalance(acct.getBalance().add(transferAmount));
            acct.setLastUpdate(LocalDateTime.now());
            accountRepository.save(acct);
            return acct;
        }).orElseThrow(() -> new AccountNotExistsException(accountId));
    }

    private void debitAccount(long accountId, BigDecimal transferAmount) throws AccountNotExistsException {
        accountRepository.findById(accountId).map(acct -> {
            if (acct.getBalance().compareTo(transferAmount) >= 0) {
                acct.setBalance(acct.getBalance().subtract(transferAmount));
                acct.setLastUpdate(LocalDateTime.now());
                accountRepository.save(acct);
            } else {
                throw new InsufficientFundsException(accountId);
            }
            return acct;
        }).orElseThrow(() -> new AccountNotExistsException(accountId));
    }

    private static final Object lock = new Object();

    @Override
    public void doTransfer(Account fromAccount, Account toAccount, BigDecimal transferAmount) throws InsufficientFundsException,
            AccountNotExistsException {
        class Helper {
            public void transfer() throws InsufficientFundsException, AccountNotExistsException {
                debitAccount(fromAccount.getId(), transferAmount);
                creditAccount(toAccount.getId(), transferAmount);
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