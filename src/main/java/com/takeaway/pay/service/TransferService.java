package com.takeaway.pay.service;

import com.takeaway.pay.domain.Transfer;
import com.takeaway.pay.exception.DailyLimitExceededException;
import com.takeaway.pay.exception.InsufficientFundsException;

import java.util.List;

public interface TransferService {
    List<Transfer> listTransfer();

    long doTranfer(Transfer transfer) throws InsufficientFundsException, DailyLimitExceededException;
}
