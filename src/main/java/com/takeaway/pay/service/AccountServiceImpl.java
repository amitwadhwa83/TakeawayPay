package com.takeaway.pay.service;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.exception.AccountNotExistsException;
import com.takeaway.pay.exception.InsufficientFundsException;
import com.takeaway.pay.repository.AccountRepository;
import com.takeaway.pay.util.AccountType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.takeaway.pay.util.AccountType.CUSTOMER;
import static com.takeaway.pay.util.AccountType.RESTAURANT;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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

    @Override
    public Account validateAndGetAccount(long accountId, AccountType accountType) throws AccountNotExistsException {
        Optional<Account> accnt = accountRepository.findById(accountId);
        if (!accnt.isPresent() ||
                (accountType.equals(CUSTOMER) && !accnt.get().isCustomer()) ||
                (accountType.equals(RESTAURANT) && accnt.get().isCustomer())) {
            throw new AccountNotExistsException(accountId, accountType);
        }
        return accnt.get();
    }
}