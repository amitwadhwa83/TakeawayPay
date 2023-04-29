package com.takeaway.pay;

import com.takeaway.pay.domain.Account;
import com.takeaway.pay.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class TakeawayPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(TakeawayPayApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(AccountRepository accountRepository) {
        return args -> {
            //Customer account
            accountRepository.save(new Account(1, new BigDecimal(40), true));
            //Restaurant account
            accountRepository.save(new Account(2, new BigDecimal(10), false));
        };
    }

}