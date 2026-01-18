package com.example.fraud.detector.seed;

import com.example.fraud.detector.entity.Merchant;
import com.example.fraud.detector.repository.*;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


@Component
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepo;
    private final AccountRepository accountRepo;
    private final MerchantRepository merchantRepo;
    private final TransactionRepository trxRepo;

    private final UserRepository userRepo;

    private final FraudResultRepository fraudResultRepo;


    public DataSeeder(CustomerRepository customerRepo,
                      AccountRepository accountRepo,
                      MerchantRepository merchantRepo,
                      TransactionRepository trxRepo,
                      UserRepository userRepo,
                      FraudResultRepository fraudResultRepo) {
        this.customerRepo = customerRepo;
        this.accountRepo = accountRepo;
        this.merchantRepo = merchantRepo;
        this.trxRepo = trxRepo;
        this.userRepo = userRepo;
        this.fraudResultRepo = fraudResultRepo;

    }

    @Override
    public void run(String... args) {

        if (merchantRepo.count() > 0) {
            System.out.println("=== Seeder skipped (data exists) ===");
            return;
        }

        Faker faker = new Faker();

        for (int i = 0; i < 5; i++) {
            merchantRepo.insertMerchant(faker.company().name(),  faker.commerce().department());

        }

        for (int i = 0; i < 10; i++) {
            userRepo.insertUser(faker.name().username());
        }

        userRepo.findAll().forEach(user -> {
            customerRepo.insertCustomer(
                    faker.name().fullName(),
                    user.getId()
            );
        });

        customerRepo.findAll().forEach(c -> {
            accountRepo.insertAccount(
                    c.getId(),
                    BigDecimal.valueOf(faker.number().numberBetween(500_000, 2_000_000)), LocalDateTime.now()
            );
        });

        accountRepo.findAll().forEach(acc -> {

            List<Merchant> merchants = merchantRepo.findAll();
            Random random = new Random();

            for (int i = 0; i < 2; i++) {
                Merchant m = merchants.get(random.nextInt(merchants.size()));

                trxRepo.insertTransaction(
                        acc.getId(),
                        m.getId(),
                        BigDecimal.valueOf(faker.number().numberBetween(100_1000, 1_000_000)),
                        LocalDateTime.now()
                );
            }
        });


        trxRepo.findAll().forEach(c -> {
            int randomInt = (int)(Math.random() * 101);
            BigDecimal score = BigDecimal.valueOf(randomInt);
            String status = randomInt >= 70 ? "FRAUD" : randomInt >= 40 ? "SUSPICIOUS" : "NORMAL";

            fraudResultRepo.insertFraudResult(score, status, c.getTransactionId());
        });


        System.out.println("=== Faker + Native SQL seeding done ===");
    }


}
