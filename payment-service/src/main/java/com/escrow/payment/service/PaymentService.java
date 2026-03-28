package com.escrow.payment.service;

import com.escrow.payment.dto.AccountResponse;
import com.escrow.payment.dto.TransactionResponse;
import com.escrow.payment.entity.*;
import com.escrow.payment.repository.AccountRepository;
import com.escrow.payment.repository.EscrowHoldRepository;
import com.escrow.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final EscrowHoldRepository escrowHoldRepository;

    @Transactional
    public AccountResponse getOrCreateAccount(UUID userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Account a = new Account();
                    a.setUserId(userId);
                    return accountRepository.save(a);
                });
        return AccountResponse.from(account);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(UUID userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return transactionRepository
                .findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(account.getId(), account.getId())
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @Transactional
    public void holdFunds(UUID dealId, UUID depositorId, BigDecimal amount) {
        // Idempotency: check if hold already exists
        if (escrowHoldRepository.findByDealId(dealId).isPresent()) {
            return;
        }

        Account depositor = getOrCreateAccountEntity(depositorId);

        if (depositor.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        // Debit depositor (pessimistic lock)
        depositor.setBalance(depositor.getBalance().subtract(amount));
        accountRepository.save(depositor);

        // Create escrow hold
        EscrowHold hold = new EscrowHold();
        hold.setDealId(dealId);
        hold.setAmount(amount);
        hold.setStatus(EscrowHoldStatus.HELD);
        escrowHoldRepository.save(hold);

        // Record transaction
        Transaction tx = new Transaction();
        tx.setDealId(dealId);
        tx.setFromAccountId(depositor.getId());
        tx.setAmount(amount);
        tx.setType(TransactionType.DEPOSIT_TO_ESCROW);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setCompletedAt(LocalDateTime.now());
        transactionRepository.save(tx);
    }

    @Transactional
    public void releaseFunds(UUID dealId, UUID beneficiaryId) {
        EscrowHold hold = escrowHoldRepository.findByDealId(dealId)
                .orElseThrow(() -> new IllegalArgumentException("No escrow hold for deal"));

        if (hold.getStatus() != EscrowHoldStatus.HELD) {
            return; // Idempotency
        }

        Account beneficiary = getOrCreateAccountEntity(beneficiaryId);

        // Credit beneficiary
        beneficiary.setBalance(beneficiary.getBalance().add(hold.getAmount()));
        accountRepository.save(beneficiary);

        // Mark hold as released
        hold.setStatus(EscrowHoldStatus.RELEASED);
        hold.setReleasedAt(LocalDateTime.now());
        escrowHoldRepository.save(hold);

        // Record transaction
        Transaction tx = new Transaction();
        tx.setDealId(dealId);
        tx.setToAccountId(beneficiary.getId());
        tx.setAmount(hold.getAmount());
        tx.setType(TransactionType.RELEASE_TO_BENEFICIARY);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setCompletedAt(LocalDateTime.now());
        transactionRepository.save(tx);
    }

    @Transactional
    public void refundFunds(UUID dealId, UUID depositorId) {
        EscrowHold hold = escrowHoldRepository.findByDealId(dealId)
                .orElseThrow(() -> new IllegalArgumentException("No escrow hold for deal"));

        if (hold.getStatus() != EscrowHoldStatus.HELD) {
            return; // Idempotency
        }

        Account depositor = getOrCreateAccountEntity(depositorId);

        // Refund depositor
        depositor.setBalance(depositor.getBalance().add(hold.getAmount()));
        accountRepository.save(depositor);

        // Mark hold as refunded
        hold.setStatus(EscrowHoldStatus.REFUNDED);
        hold.setReleasedAt(LocalDateTime.now());
        escrowHoldRepository.save(hold);

        // Record transaction
        Transaction tx = new Transaction();
        tx.setDealId(dealId);
        tx.setToAccountId(depositor.getId());
        tx.setAmount(hold.getAmount());
        tx.setType(TransactionType.REFUND_TO_DEPOSITOR);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setCompletedAt(LocalDateTime.now());
        transactionRepository.save(tx);
    }

    private Account getOrCreateAccountEntity(UUID userId) {
        return accountRepository.findByUserIdForUpdate(userId)
                .orElseGet(() -> {
                    Account a = new Account();
                    a.setUserId(userId);
                    return accountRepository.save(a);
                });
    }
}
