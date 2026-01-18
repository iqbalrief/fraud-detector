package com.example.fraud.detector.controller;

import com.example.fraud.detector.entity.FraudResult;
import com.example.fraud.detector.repository.AccountRepository;
import com.example.fraud.detector.repository.MerchantRepository;
import com.example.fraud.detector.repository.TransactionRepository;
import com.example.fraud.detector.service.FraudService;
import com.example.fraud.detector.entity.Transaction;
import org.springframework.web.bind.annotation.*;
import com.example.fraud.detector.dto.TransactionRequestDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/fraud")
public class FraudController {

    private final FraudService fraudService;
    private final AccountRepository accountRepo;
    private final MerchantRepository merchantRepo;
    private final TransactionRepository transactionRepo;




    public FraudController(FraudService fraudService,
                           AccountRepository accountRepo,
                           MerchantRepository merchantRepo, TransactionRepository transactionRepo) {

        this.fraudService = fraudService;
        this.accountRepo = accountRepo;
        this.merchantRepo = merchantRepo;
        this.transactionRepo = transactionRepo;
    }


    @PostMapping()
    public FraudResult checkFraud(
            @RequestHeader("apiKey") String apiKey,
            @RequestBody TransactionRequestDto req) {

        Transaction trx = new Transaction();
        trx.setAccount(
                accountRepo.findById(req.getAccountId()).orElseThrow()
        );
        trx.setMerchant(
                merchantRepo.findById(req.getMerchantId()).orElseThrow()
        );
        trx.setAmount(req.getAmount());
        trx.setAmount(req.getAmount());

        return fraudService.checkFraud(trx, apiKey);
    }

    @GetMapping()
    public Map<String, Object> getAllData() {
        Map<String, Object> result = new HashMap<>();
        result.put("transactions", fraudService.getAllTransactions());

        return result;
    }

    @GetMapping("/all")
    public List<FraudResult> getAllFrauds() {
        return fraudService.findAllFraudDetail();
    }



}
