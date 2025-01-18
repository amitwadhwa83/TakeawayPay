package com.takeaway.pay.service;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.domain.Transfer;
import com.takeaway.pay.exception.*;
import com.takeaway.pay.repository.TransferRepository;
import com.takeaway.pay.service.AccountService;
import com.takeaway.pay.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TransferServiceImplTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransferServiceImpl transferService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListTransfer() {
        Transfer transfer = new Transfer();
        when(transferRepository.findAll()).thenReturn(Arrays.asList(transfer));

        List<Transfer> result = transferService.listTransfer();
        assertEquals(Arrays.asList(transfer), result);
    }

    @Test
    public void testDoTransfer() throws Exception {
        Transfer transfer = new Transfer(1L, 2L, BigDecimal.valueOf(100));
        Account sourceAccount = new Account(1L, BigDecimal.valueOf(1000), true);
        Account destAccount = new Account(2L, BigDecimal.valueOf(2000), true);
        List<Transfer> transfersForToday = Arrays.asList();

        when(accountService.findById(1L)).thenReturn(sourceAccount);
        when(accountService.findById(2L)).thenReturn(destAccount);
        when(transferRepository.findByDate(1L, LocalDate.now())).thenReturn(transfersForToday);
        when(transferRepository.save(any(Transfer.class))).thenReturn(1L);

        long result = transferService.doTranfer(transfer);
        assertEquals(1L, result);
    }

    @Test
    public void testDoTransferThrowsException() throws Exception {
        Transfer transfer = new Transfer(1L, 2L, BigDecimal.valueOf(100));
        Account sourceAccount = new Account(1L, BigDecimal.valueOf(1000), true);
        Account destAccount = new Account(2L, BigDecimal.valueOf(2000), true);
        List<Transfer> transfersForToday = Arrays.asList();

        when(accountService.findById(1L)).thenReturn(sourceAccount);
        when(accountService.findById(2L)).thenReturn(destAccount);
        when(transferRepository.findByDate(1L, LocalDate.now())).thenReturn(transfersForToday);
        doThrow(new InsufficientFundsException(1L)).when(accountService).doTransfer(sourceAccount, destAccount, BigDecimal.valueOf(100));

        assertThrows(InsufficientFundsException.class, () -> transferService.doTranfer(transfer));
    }
}
