package com.takeaway.pay.controller;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.domain.Transfer;
import com.takeaway.pay.exception.DailyLimitExceededException;
import com.takeaway.pay.exception.InsufficientFundsException;
import com.takeaway.pay.exception.InvalidAccountException;
import com.takeaway.pay.exception.InvalidAmountException;
import com.takeaway.pay.service.AccountService;
import com.takeaway.pay.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @PostMapping(value = "/transfer/create/{customerAccount}/{restaurantAccount}/{amount}")
    public long tranferToRestaurant(@PathVariable("customerAccount") long customerAccount,
                                    @PathVariable("restaurantAccount") long restaurantAccount,
                                    @PathVariable("amount") BigDecimal amount) throws InsufficientFundsException,
            InvalidAmountException, InvalidAccountException, DailyLimitExceededException {
        return transferService.tranferToRestaurant(customerAccount, restaurantAccount, amount);
    }
}