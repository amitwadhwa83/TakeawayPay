package com.takeaway.pay.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//@Entity
//@Table(name = "account")
public class Account {
    // @Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE)
    // @Column(name = "id", nullable = false)
    private long id;

    // @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    // @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;

    // @Column(name = "is_customer", nullable = false)
    private boolean customer;

    //  public Account() {
    // }

    public Account(long id, BigDecimal balance, boolean isCustomer) {
        this.id = id;
        this.balance = balance;
        this.customer = isCustomer;
        lastUpdate = LocalDateTime.now();
    }

    public Account(boolean isCustomer) {
        this.customer = isCustomer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isCustomer() {
        return customer;
    }

    @JsonIgnore
    public boolean isNotCustomerAccount() {
        return !customer;
    }

    @JsonIgnore
    public boolean isCustomerAccount() {
        return customer;
    }

    public void setCustomer(boolean customer) {
        this.customer = customer;
    }
}