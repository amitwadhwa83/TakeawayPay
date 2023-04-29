package com.takeaway.pay.repository;


import com.takeaway.pay.domain.Transfer;

import java.time.LocalDate;
import java.util.List;

public interface TransferRepository {
    //@Query(value = "SELECT t.* FROM transfer t WHERE source_account =:sourceAccount and CAST(last_update AS DATE) =:date", nativeQuery = true)
    List<Transfer> findByDate(long sourceAccount, LocalDate date);

    List<Transfer> findAll();

    long save(Transfer transfer);
}
