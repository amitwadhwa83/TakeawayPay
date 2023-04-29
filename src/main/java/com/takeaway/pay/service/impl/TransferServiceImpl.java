package com.takeaway.pay.service.impl;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.domain.Transfer;
import com.takeaway.pay.exception.*;
import com.takeaway.pay.repository.TransferRepository;
import com.takeaway.pay.service.AccountService;
import com.takeaway.pay.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.takeaway.pay.business.TranferValidation.validateTransfer;

@Service
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
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
            InvalidAmountException, InvalidTransferException {

        Account sourceAccount = accountService.findById(transfer.getSourceAccount());
        Account destAccount = accountService.findById(transfer.getDestAccount());
        List<Transfer> transfersForToday = transferRepository.findByDate(transfer.getSourceAccount(), LocalDate.now());
        validateTransfer(sourceAccount, destAccount, transfer.getAmount(), transfersForToday);
        return tranferMoney(sourceAccount, destAccount, transfer.getAmount());
    }


    private long tranferMoney(Account fromAccount, Account toAccount, BigDecimal transferAmount) throws InsufficientFundsException,
            AccountNotExistsException {
        accountService.doTransfer(fromAccount, toAccount, transferAmount);
        return transferRepository.save(new Transfer(fromAccount.getId(), toAccount.getId(), transferAmount));
    }
}