package com.takeaway.pay.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;


//@Entity
//@Table(name = "transfer")
public class Transfer {

    //@Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE)
    // @Column(name = "id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    // @Column(name = "source_account", nullable = false)
    @NotNull
    private long sourceAccount;

    //@Column(name = "dest_account", nullable = false)
    @NotNull
    private long destAccount;

   // @Column(name = "amount", nullable = false)
    @NotNull
    private BigDecimal amount;

    //@Column(name = "last_update")
    private LocalDateTime lastUpdate;

    public Transfer() {
    }

    public Transfer(long sourceAccount, long destAccount, BigDecimal amount) {
        this.sourceAccount = sourceAccount;
        this.destAccount = destAccount;
        this.amount = amount;
        lastUpdate = LocalDateTime.now();
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