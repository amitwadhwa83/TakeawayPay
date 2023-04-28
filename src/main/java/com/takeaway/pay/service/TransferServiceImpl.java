package com.takeaway.pay.service;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.domain.Transfer;
import com.takeaway.pay.exception.*;
import com.takeaway.pay.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.takeaway.pay.business.TranferValidation.validateTransaction;
import static com.takeaway.pay.util.AccountType.CUSTOMER;
import static com.takeaway.pay.util.AccountType.RESTAURANT;

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
    @Transactional(rollbackFor = Exception.class)
    public long doTranfer(Transfer transfer)
            throws AccountNotExistsException, DailyLimitExceededException, IdenticalAccountsException, InsufficientFundsException,
            InvalidAmountException {
        BigDecimal transferAmount = transfer.getAmount();
        List<Transfer> transfersForToday = transferRepository.findByDate(transfer.getSourceAccount(), LocalDate.now());
        validateTransaction(transfer, transfersForToday);

        Account customerAccount = accountService.validateAndGetAccount(transfer.getSourceAccount(), CUSTOMER);
        Account restaurantAccount = accountService.validateAndGetAccount(transfer.getDestAccount(), RESTAURANT);
        return tranferMoney(customerAccount, restaurantAccount, transferAmount);
    }

    public long tranferMoney(Account fromAccount, Account toAccount, BigDecimal transferAmount) throws InsufficientFundsException,
            AccountNotExistsException {
        accountService.doTransfer(fromAccount, toAccount, transferAmount);
        return transferRepository.save(new Transfer(fromAccount.getId(), toAccount.getId(), transferAmount))
                .getId();
    }
}