package com.takeaway.pay.controller;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.domain.Transfer;
import com.takeaway.pay.exception.*;
import com.takeaway.pay.service.AccountService;
import com.takeaway.pay.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class TransferControllerTest {

    @Mock
    private TransferService transferService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransferController transferController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListTransfer() {
        Transfer transfer = new Transfer();
        when(transferService.listTransfer()).thenReturn(Arrays.asList(transfer));

        Iterable<Transfer> result = transferController.listTransfer();
        assertEquals(Arrays.asList(transfer), result);
    }

    @Test
    public void testListAccount() {
        Account account = new Account(1L, BigDecimal.valueOf(1L), true);
        when(accountService.listAccount()).thenReturn(Arrays.asList(account));

        List<Account> result = transferController.listAccount();
        assertEquals(Arrays.asList(account), result);
    }

    @Test
    public void testDoTransfer() throws IdenticalAccountsException, InsufficientFundsException, InvalidAmountException, InvalidTransferException, AccountNotExistsException, DailyLimitExceededException {
        Transfer transfer = new Transfer();
        when(transferService.doTranfer(any(Transfer.class))).thenReturn(1L);

        ResponseEntity<?> response = transferController.doTranfer(transfer);
        assertEquals("Transfer id:1", response.getBody());
    }

    @Test
    public void testDoTransferException() throws IdenticalAccountsException, InsufficientFundsException, InvalidAmountException, InvalidTransferException, AccountNotExistsException, DailyLimitExceededException {
        Transfer transfer = new Transfer();
        when(transferService.doTranfer(any(Transfer.class))).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = transferController.doTranfer(transfer);
        assertEquals("Transfer failed with reason :Error", response.getBody());
    }
}