package com.takeaway.pay.domain;

import jakarta.persistence.Table;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "transfer")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "source_account", nullable = false)
    private long sourceAccount;
    @Column(name = "dest_account", nullable = false)
    private long destAccount;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    public Transfer() {
    }

    public Transfer(long sourceAccount, long destAccount, BigDecimal amount) {
        this.sourceAccount = sourceAccount;
        this.destAccount = destAccount;
        this.amount = amount;
        lastUpdate=LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(long sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public long getDestAccount() {
        return destAccount;
    }

    public void setDestAccount(long destAccount) {
        this.destAccount = destAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}