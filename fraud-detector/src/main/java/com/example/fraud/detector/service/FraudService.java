package com.example.fraud.detector.service;

import com.example.fraud.detector.dto.TransactionDto;
import com.example.fraud.detector.entity.Account;
import com.example.fraud.detector.entity.FraudResult;
import com.example.fraud.detector.entity.Merchant;
import com.example.fraud.detector.entity.Transaction;
import com.example.fraud.detector.exception.GlobalException;
import com.example.fraud.detector.entity.*;
import com.example.fraud.detector.models.FraudIndicatorResult;
import com.example.fraud.detector.repository.*;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class FraudService {

    private final TransactionRepository transactionRepository;
    private final FraudResultRepository fraudResultRepository;

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    private final MerchantRepository merchantRepository;

    public FraudService(TransactionRepository transactionRepository, FraudResultRepository fraudResultRepository, UserRepository userRepository, AccountRepository accountRepository, MerchantRepository merchantRepository) {
        this.transactionRepository = transactionRepository;
        this.fraudResultRepository = fraudResultRepository;

        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.merchantRepository = merchantRepository;
    }


    @Transactional
    public FraudResult evaluateFraud(Transaction trx) {

        Long accountId = trx.getAccount().getId();
        Long merchantId = trx.getMerchant().getId();

        BigDecimal avgAmount =
                transactionRepository.getAverageAmount(accountId);

        if (avgAmount == null) {
            avgAmount = BigDecimal.ZERO;
        }

        List<FraudIndicatorResult> indicators = List.of(
                new FraudIndicatorResult(
                        "FREQUENT_TRANSACTION",
                        70,
                        transactionRepository.countRecentTransaction(accountId) > 5
                ),
                new FraudIndicatorResult(
                        "SAME_AMOUNT_REPEAT",
                        30,
                        transactionRepository.countSameAmount(accountId, trx.getAmount().doubleValue()) > 3
                ),
                new FraudIndicatorResult(
                        "NEW_MERCHANT",
                        30,
                        transactionRepository.countMerchantUsage(accountId, merchantId) == 0
                ),


                new FraudIndicatorResult(
                        "ABOVE_AVERAGE_AMOUNT",
                        50,
                        trx.getAmount().compareTo(avgAmount) > 0
                )
        );

        // JAVA STREAM
        int totalScore = indicators.stream()
                .filter(FraudIndicatorResult::isTriggered)
                .mapToInt(FraudIndicatorResult::getScore)
                .sum();

        String status = totalScore >= 70 ? "FRAUD" :
                totalScore >= 40 ? "SUSPICIOUS" : "NORMAL";




        FraudResult result = new FraudResult();
        result.setTransaction(trx);
        result.setFraudScore(totalScore);
        result.setFraudStatus(status);

        return result;
    }

    public FraudResult checkFraud(Transaction trx, String apiKey) {

        Long userId = userRepository.findByApiKey(apiKey);
        if (userId == null) throw new GlobalException("Invalid Api Key", GlobalException.ErrorType.UNAUTHORIZED);

        int belongs = accountRepository.countAccountBelongsToUser(trx.getAccount().getId(), userId);
        if (belongs == 0) {
            throw new GlobalException("Account does not belong to user", GlobalException.ErrorType.UNAUTHORIZED);
        }

        Account account = accountRepository.findById(trx.getAccount().getId())
                .orElseThrow(() -> new GlobalException("Account not found", GlobalException.ErrorType.NOT_FOUND));

        Merchant merchant = merchantRepository.findById(trx.getMerchant().getId())
                .orElseThrow(() -> new GlobalException("Merchant not found", GlobalException.ErrorType.NOT_FOUND));

        trx = transactionRepository.save(trx);

        FraudResult result = evaluateFraud(trx);

        fraudResultRepository.insertFraudResult(
                BigDecimal.valueOf(result.getFraudScore()),
                result.getFraudStatus(),
                trx.getTransactionId()
        );

        BigDecimal balance = account.getBalance();

        if (trx.getAmount().compareTo(balance) > 0) {
            throw new GlobalException(
                    "Insufficient balance",
                    GlobalException.ErrorType.INSUFFICIENT_BALANCE
            );
        }

        if ("NORMAL".equals(result.getFraudStatus())) {
            accountRepository.deductBalance(account.getId(), trx.getAmount());
        }


        return result;
    }

    public List<TransactionDto> getAllTransactions() {
        // pakai native query yang join ke fraud_results
        List<Object[]> rows = transactionRepository.findAllTransactionsWithFraud();

        return rows.stream().map(r -> new TransactionDto(
                ((Number) r[0]).longValue(),
                ((Number) r[1]).longValue(),
                ((Number) r[2]).longValue(),
                (BigDecimal) r[3],
                ((LocalDateTime) r[4]),
                r[5] != null ? (String) r[5] : null,
                r[6] != null
                        ? BigDecimal.valueOf(((Number) r[6]).longValue())
                        : null
        )).toList();
    }

    public List<FraudResult> findAllFraudDetail() {
        return fraudResultRepository.findAllWithDetail();
    }

}

