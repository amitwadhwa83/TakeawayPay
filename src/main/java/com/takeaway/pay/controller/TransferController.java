package com.takeaway.pay.controller;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.domain.Transfer;
import com.takeaway.pay.service.AccountService;
import com.takeaway.pay.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountService accountService;

    @GetMapping(value = "/transfers")
    public Iterable<Transfer> listTransfer() {
        return transferService.listTransfer();
    }

    @GetMapping("/accounts")
    public List<Account> listAccount() {
        return accountService.listAccount();
    }

    @PostMapping(value = "/transfer/create")
    public ResponseEntity<?> doTranfer(@Valid @RequestBody Transfer transfer) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(transferService.doTranfer(transfer));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}