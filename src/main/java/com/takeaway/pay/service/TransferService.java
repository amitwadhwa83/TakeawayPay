package com.takeaway.pay.service;

import com.takeaway.pay.domain.Transfer;
import com.takeaway.pay.exception.DailyLimitExceededException;
import com.takeaway.pay.exception.InsufficientFundsException;
import com.takeaway.pay.exception.InvalidAccountException;
import com.takeaway.pay.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.util.List;

public interface TransferService {
    List<Transfer> listTransfer();

    long tranferToRestaurant(Transfer transfer) throws InsufficientFundsException,
            InvalidAmountException, InvalidAccountException, DailyLimitExceededException;
}
