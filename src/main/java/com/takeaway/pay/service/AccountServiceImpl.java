package com.takeaway.pay.service;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.exception.InsufficientFundsException;
import com.takeaway.pay.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Override
    public void creditRestaurantAccount(long accountId, BigDecimal amount) {
        Optional<Account> account = accountRepository.findById(accountId);
        account.ifPresent(acct -> {
            acct.setBalance(acct.getBalance().add(amount));
            acct.setLastUpdate(LocalDateTime.now());
            accountRepository.save(acct);
        });
    }

    @Override
    public void debitCustomerAccount(long accountId, BigDecimal amount) throws InsufficientFundsException {
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
}