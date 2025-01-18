package com.takeaway.pay.service;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.exception.AccountNotExistsException;
import com.takeaway.pay.exception.InsufficientFundsException;
import com.takeaway.pay.repository.AccountRepository;
import com.takeaway.pay.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById() throws AccountNotExistsException {
        Account account = new Account(1L, BigDecimal.valueOf(1000), true);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Account result = accountService.findById(1L);
        assertEquals(account, result);
    }

    @Test
    public void testFindByIdNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotExistsException.class, () -> accountService.findById(1L));
    }

    @Test
    public void testListAccount() {
        Account account1 = new Account(1L, BigDecimal.valueOf(1000), true);
        Account account2 = new Account(2L, BigDecimal.valueOf(2000), true);
        when(accountRepository.findAll()).thenReturn(Arrays.asList(account1, account2));

        List<Account> result = accountService.listAccount();
        assertEquals(Arrays.asList(account1, account2), result);
    }

    @Test
    public void testDoTransfer() throws InsufficientFundsException, AccountNotExistsException {
        Account fromAccount = new Account(1L, BigDecimal.valueOf(1000), true);
        Account toAccount = new Account(2L, BigDecimal.valueOf(2000), true);
        BigDecimal transferAmount = BigDecimal.valueOf(500);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        accountService.doTransfer(fromAccount, toAccount, transferAmount);

        assertEquals(BigDecimal.valueOf(500), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(2500), toAccount.getBalance());
        verify(accountRepository, times(1)).save(fromAccount);
        verify(accountRepository, times(1)).save(toAccount);
    }

    @Test
    public void testDoTransferInsufficientFunds() {
        Account fromAccount = new Account(1L, BigDecimal.valueOf(100), true);
        Account toAccount = new Account(2L, BigDecimal.valueOf(2000), true);
        BigDecimal transferAmount = BigDecimal.valueOf(500);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        assertThrows(InsufficientFundsException.class, () -> accountService.doTransfer(fromAccount, toAccount, transferAmount));
    }
}
