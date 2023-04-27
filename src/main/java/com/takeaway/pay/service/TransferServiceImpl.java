package com.takeaway.pay.service;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.domain.Transfer;
import com.takeaway.pay.exception.DailyLimitExceededException;
import com.takeaway.pay.exception.InsufficientFundsException;
import com.takeaway.pay.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.takeaway.pay.util.AccountType.CUSTOMER;
import static com.takeaway.pay.util.AccountType.RESTAURANT;
import static com.takeaway.pay.util.business.TranferValidators.validateTransaction;

@Service
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    @Autowired
    private final AccountService accountService;


    public TransferServiceImpl(TransferRepository transferRepository, AccountService accountService) {
        this.transferRepository = transferRepository;
        this.accountService = accountService;
    }

    @Override
    public List<Transfer> listTransfer() {
        return transferRepository.findAll();
    }

    @Override
    public long doTranfer(Transfer transfer)
            throws InsufficientFundsException, DailyLimitExceededException {
        long debitAccount = transfer.getSourceAccount();
        long creditAccount = transfer.getDestAccount();
        BigDecimal transferAmount = transfer.getAmount();
        List<Transfer> transfersForToday = transferRepository.findByDate(debitAccount, LocalDate.now());
        validateTransaction(debitAccount, creditAccount, transferAmount, transfersForToday);

        Account customerAccount = accountService.validateAndGetAccount(CUSTOMER, debitAccount);
        Account restaurantAccount = accountService.validateAndGetAccount(RESTAURANT, creditAccount);
        return tranferMoney(customerAccount, restaurantAccount, transferAmount);
    }


    private long tranferMoney(Account fromAccount, Account toAccount, BigDecimal amount) throws InsufficientFundsException {
        accountService.doTransfer(fromAccount, toAccount, amount);
        return transferRepository.save(new Transfer(fromAccount.getId(), toAccount.getId(), amount))
                .getId();
    }
}