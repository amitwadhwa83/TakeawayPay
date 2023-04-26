package com.takeaway.pay.service;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.exception.GenericException;
import com.takeaway.pay.exception.InsufficientFundsException;
import com.takeaway.pay.exception.InvalidAccountException;
import com.takeaway.pay.repository.AccountRepository;
import com.takeaway.pay.util.AccountType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.takeaway.pay.util.AccountType.RESTAURANT;
import static com.takeaway.pay.util.AccountType.CUSTOMER;

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

    @Override
    public Optional<Account> findById(long accountId) {
        return accountRepository.findById(accountId);
    }

    private void creditAccount(long accountId, BigDecimal amount) {
        Optional<Account> account = accountRepository.findById(accountId);
        account.ifPresent(acct -> {
            acct.setBalance(acct.getBalance().add(amount));
            acct.setLastUpdate(LocalDateTime.now());
            accountRepository.save(acct);
        });
    }

    private void debitAccount(long accountId, BigDecimal amount) throws InsufficientFundsException {
        Optional<Account> account = accountRepository.findById(accountId);
        account.ifPresent(acct -> {
            if (acct.getBalance().compareTo(amount) >= 0) {
                acct.setBalance(acct.getBalance().subtract(amount));
                acct.setLastUpdate(LocalDateTime.now());
                accountRepository.save(acct);
            } else {
                try {
                    throw new InsufficientFundsException(accountId);
                } catch (InsufficientFundsException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static final Object lock = new Object();

    @Override
    @Transactional(rollbackFor = GenericException.class)
    public void doTransfer(Account fromAccount, Account toAccount, BigDecimal amount) throws InsufficientFundsException {
        class Helper {
            public void transfer() throws InsufficientFundsException {
                debitAccount(fromAccount.getId(), amount);
                creditAccount(toAccount.getId(), amount);
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
    public Account validateAndGetAccount(AccountType accountType, long account) throws InvalidAccountException {
        Optional<Account> accnt = accountRepository.findById(account);
        if (!accnt.isPresent() ||
                (accountType.equals(CUSTOMER) && !accnt.get().isCustomer()) ||
                (accountType.equals(RESTAURANT) && accnt.get().isCustomer())) {
            throw new InvalidAccountException("Account not found or invalid:" + account);
        }
        return accnt.get();
    }
}