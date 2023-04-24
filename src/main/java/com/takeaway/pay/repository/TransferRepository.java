package com.takeaway.pay.repository;


import com.takeaway.pay.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query(value = "SELECT t.* FROM transfer t WHERE source_account =:sourceAccount and CAST(last_update AS DATE) =:date", nativeQuery = true)
    List<Transfer> findByDate(long sourceAccount, LocalDate date);
}
