package com.takeaway.pay.repository.impl;

import com.takeaway.pay.domain.Transfer;
import com.takeaway.pay.repository.TransferRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class TransferRepositoryImpl implements TransferRepository {
    private final Map<Long, Transfer> transfers = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(0L);

    @Override
    public List<Transfer> findByDate(long sourceAccount, LocalDate date) {
        return transfers.entrySet()
                .stream()
                .filter(transfer ->
                        (transfer.getValue().getSourceAccount() == sourceAccount
                                && transfer.getValue().getLastUpdate().toLocalDate().isEqual(date)))
                .map(transfer -> transfer.getValue())
                .collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findAll() {
        return transfers.entrySet()
                .stream()
                .map(transfer -> transfer.getValue())
                .collect(Collectors.toList());
    }

    @Override
    public long save(Transfer transfer) {
        transfer.setId(counter.incrementAndGet());
        transfers.putIfAbsent(transfer.getId(), transfer);
        return transfer.getId();
    }
}
