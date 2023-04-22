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
        Account account = accountRepository.getReferenceById(accountId);
        account.setBalance(account.getBalance().add(amount));
        account.setLastUpdate(LocalDateTime.now());
        accountRepository.save(account);
    }

    @Override
    public void debitCustomerAccount(long accountId, BigDecimal amount) throws InsufficientFundsException {
        Account account = accountRepository.getReferenceById(accountId);
        if (account.getBalance().compareTo(amount) >= 0) {
            account.setBalance(account.getBalance().subtract(amount));
            account.setLastUpdate(LocalDateTime.now());
            accountRepository.save(account);
        } else {
            throw new InsufficientFundsException(accountId);
        }
    }
}
