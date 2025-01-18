package com.takeaway.pay.repository.impl;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.repository.impl.AccountRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AccountRepositoryImplTest {

    private AccountRepositoryImpl accountRepository;

    @BeforeEach
    public void setUp() {
        accountRepository = new AccountRepositoryImpl();
    }

    @Test
    public void testSaveAndFindById() {
        Account account = new Account(1L, BigDecimal.valueOf(1000), true);
        accountRepository.save(account);

        Optional<Account> foundAccount = accountRepository.findById(1L);
        assertTrue(foundAccount.isPresent());
        assertEquals(account, foundAccount.get());
    }

    @Test
    public void testFindByIdNotFound() {
        Optional<Account> foundAccount = accountRepository.findById(1L);
        assertFalse(foundAccount.isPresent());
    }

    @Test
    public void testFindAll() {
        Account account1 = new Account(1L, BigDecimal.valueOf(1000), true);
        Account account2 = new Account(2L, BigDecimal.valueOf(2000), true);
        accountRepository.save(account1);
        accountRepository.save(account2);

        List<Account> accounts = accountRepository.findAll();
        assertEquals(2, accounts.size());
        assertTrue(accounts.contains(account1));
        assertTrue(accounts.contains(account2));
    }
}
