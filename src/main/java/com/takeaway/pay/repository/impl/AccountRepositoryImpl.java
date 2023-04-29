package com.takeaway.pay.repository.impl;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.repository.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class AccountRepositoryImpl implements AccountRepository {
    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public Optional<Account> findById(long id) {
        return Optional.ofNullable(accounts.get(id));
    }

    @Override
    public List<Account> findAll() {
        return accounts.entrySet()
                .stream()
                .map(account -> account.getValue())
                .collect(Collectors.toList());
    }

    @Override
    public void save(Account account) {
        accounts.putIfAbsent(account.getId(), account);
    }
}
