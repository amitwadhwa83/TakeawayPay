package com.takeaway.pay.repository;

import com.takeaway.pay.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findById(long id);

    List<Account> findAll();

    void save(Account account);
}
