package com.escrow.payment.service;

import com.escrow.payment.dto.AccountResponse;
import com.escrow.payment.dto.EscrowAccountResponse;
import com.escrow.payment.dto.TransactionResponse;
import com.escrow.payment.entity.*;
import com.escrow.payment.repository.*;
import com.escrow.payment.statemachine.EscrowAccountStateMachine;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final EscrowHoldRepository escrowHoldRepository;
    private final LedgerService ledgerService;
    private final LedgerAccountRepository ledgerAccountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final EscrowAccountRepository escrowAccountRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    // ── Public API ──────────────────────────────────────────────

    @Transactional
    public AccountResponse getOrCreateAccount(UUID userId) {
        LedgerAccount ledgerAccount = ledgerService.getOrCreateAvailableAccount(userId, "RUB");
        Account account = accountRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Account a = new Account();
                    a.setUserId(userId);
                    return accountRepository.save(a);
                });
        account.setBalance(ledgerAccount.getBalance());
        accountRepository.save(account);
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

    @Transactional(readOnly = true)
    public EscrowAccountResponse getEscrowAccount(UUID dealId) {
        EscrowAccount ea = escrowAccountRepository.findByDealId(dealId)
                .orElseThrow(() -> new IllegalArgumentException("Escrow account not found for deal"));
        return EscrowAccountResponse.from(ea);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLedgerEntries(UUID dealId) {
        List<Transaction> txs = transactionRepository.findByDealIdOrderByCreatedAtDesc(dealId);
        return txs.stream().flatMap(tx ->
                ledgerEntryRepository.findByTransactionIdOrderByCreatedAtAsc(tx.getId()).stream()
                        .map(entry -> Map.<String, Object>of(
                                "id", entry.getId(),
                                "transactionId", entry.getTransactionId(),
                                "accountId", entry.getAccountId(),
                                "entryType", entry.getEntryType(),
                                "amount", entry.getAmount(),
                                "balanceAfter", entry.getBalanceAfter(),
                                "createdAt", entry.getCreatedAt()
                        ))
        ).toList();
    }

    // ── Internal API (called by deal-service) ───────────────────

    @Transactional
    public void holdFunds(UUID dealId, UUID depositorId, BigDecimal amount, String idempotencyKey) {
        // Idempotency check
        if (idempotencyKey != null && idempotencyKeyRepository.findById(idempotencyKey).isPresent()) {
            log.info("Idempotent hold request, key={}", idempotencyKey);
            return;
        }
        if (escrowHoldRepository.findByDealId(dealId).isPresent()) {
            return;
        }

        // 1. Create escrow account + state machine
        EscrowAccount escrowAccount = escrowAccountRepository.findByDealId(dealId)
                .orElseGet(() -> {
                    EscrowAccount ea = new EscrowAccount();
                    ea.setDealId(dealId);
                    ea.setDepositorId(depositorId);
                    ea.setBeneficiaryId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
                    ea.setAmount(amount);
                    ea.setStatus(EscrowAccountStatus.OPENED);
                    return escrowAccountRepository.save(ea);
                });

        EscrowAccountStateMachine.validate(escrowAccount.getStatus(), EscrowAccountStatus.FUNDS_DEPOSITING);
        escrowAccount.setStatus(EscrowAccountStatus.FUNDS_DEPOSITING);
        escrowAccountRepository.save(escrowAccount);

        // 2. Double-entry ledger: depositor AVAILABLE → SYSTEM ESCROW_HOLDING
        LedgerAccount depositorAvailable = ledgerService.getOrCreateAvailableAccount(depositorId, "RUB");
        UUID txId = UUID.randomUUID();
        ledgerService.transfer(depositorAvailable.getId(), ledgerService.getSystemEscrowAccountId(), amount, txId);

        // 3. Escrow SM: FUNDS_DEPOSITING → FUNDS_SECURED
        escrowAccount.setStatus(EscrowAccountStatus.FUNDS_SECURED);
        escrowAccount.setFundedAt(LocalDateTime.now());
        escrowAccountRepository.save(escrowAccount);

        // 4. Legacy tables (backward compat)
        Account depositorLegacy = getOrCreateAccountEntity(depositorId);
        depositorLegacy.setBalance(depositorLegacy.getBalance().subtract(amount));
        accountRepository.save(depositorLegacy);

        EscrowHold hold = new EscrowHold();
        hold.setDealId(dealId);
        hold.setAmount(amount);
        hold.setStatus(EscrowHoldStatus.HELD);
        escrowHoldRepository.save(hold);

        Transaction tx = new Transaction();
        tx.setDealId(dealId);
        tx.setFromAccountId(depositorLegacy.getId());
        tx.setAmount(amount);
        tx.setType(TransactionType.DEPOSIT_TO_ESCROW);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setCompletedAt(LocalDateTime.now());
        transactionRepository.save(tx);

        // 5. Outbox event
        saveOutboxEvent("EscrowAccount", dealId, "FUNDS_SECURED",
                Map.of("dealId", dealId, "depositorId", depositorId, "amount", amount));

        if (idempotencyKey != null) saveIdempotencyKey(idempotencyKey, "HELD");
        log.info("Funds held (double-entry): dealId={}, amount={}", dealId, amount);
    }

    /** Backward-compatible overload for existing deal-service calls */
    @Transactional
    public void holdFunds(UUID dealId, UUID depositorId, BigDecimal amount) {
        holdFunds(dealId, depositorId, amount, null);
    }

    @Transactional
    public void releaseFunds(UUID dealId, UUID beneficiaryId, String idempotencyKey) {
        if (idempotencyKey != null && idempotencyKeyRepository.findById(idempotencyKey).isPresent()) {
            return;
        }

        EscrowHold hold = escrowHoldRepository.findByDealId(dealId)
                .orElseThrow(() -> new IllegalArgumentException("No escrow hold for deal"));
        if (hold.getStatus() != EscrowHoldStatus.HELD) return;

        EscrowAccount escrowAccount = escrowAccountRepository.findByDealId(dealId)
                .orElseThrow(() -> new IllegalStateException("No escrow account for deal"));

        // SM: → RELEASING → RELEASED_TO_BENEFICIARY
        EscrowAccountStateMachine.validate(escrowAccount.getStatus(), EscrowAccountStatus.RELEASING);
        escrowAccount.setStatus(EscrowAccountStatus.RELEASING);
        escrowAccountRepository.save(escrowAccount);

        // Double-entry: SYSTEM ESCROW_HOLDING → beneficiary AVAILABLE
        LedgerAccount beneficiaryAvailable = ledgerService.getOrCreateAvailableAccount(beneficiaryId, "RUB");
        UUID txId = UUID.randomUUID();
        ledgerService.transfer(ledgerService.getSystemEscrowAccountId(), beneficiaryAvailable.getId(), hold.getAmount(), txId);

        escrowAccount.setStatus(EscrowAccountStatus.RELEASED_TO_BENEFICIARY);
        escrowAccount.setReleasedAt(LocalDateTime.now());
        escrowAccountRepository.save(escrowAccount);

        // Legacy
        Account beneficiary = getOrCreateAccountEntity(beneficiaryId);
        beneficiary.setBalance(beneficiary.getBalance().add(hold.getAmount()));
        accountRepository.save(beneficiary);

        hold.setStatus(EscrowHoldStatus.RELEASED);
        hold.setReleasedAt(LocalDateTime.now());
        escrowHoldRepository.save(hold);

        Transaction tx = new Transaction();
        tx.setDealId(dealId);
        tx.setToAccountId(beneficiary.getId());
        tx.setAmount(hold.getAmount());
        tx.setType(TransactionType.RELEASE_TO_BENEFICIARY);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setCompletedAt(LocalDateTime.now());
        transactionRepository.save(tx);

        saveOutboxEvent("EscrowAccount", dealId, "RELEASED_TO_BENEFICIARY",
                Map.of("dealId", dealId, "beneficiaryId", beneficiaryId, "amount", hold.getAmount()));

        if (idempotencyKey != null) saveIdempotencyKey(idempotencyKey, "RELEASED");
        log.info("Funds released (double-entry): dealId={}, beneficiaryId={}", dealId, beneficiaryId);
    }

    @Transactional
    public void releaseFunds(UUID dealId, UUID beneficiaryId) {
        releaseFunds(dealId, beneficiaryId, null);
    }

    @Transactional
    public void refundFunds(UUID dealId, UUID depositorId, String idempotencyKey) {
        if (idempotencyKey != null && idempotencyKeyRepository.findById(idempotencyKey).isPresent()) {
            return;
        }

        EscrowHold hold = escrowHoldRepository.findByDealId(dealId)
                .orElseThrow(() -> new IllegalArgumentException("No escrow hold for deal"));
        if (hold.getStatus() != EscrowHoldStatus.HELD) return;

        EscrowAccount escrowAccount = escrowAccountRepository.findByDealId(dealId)
                .orElseThrow(() -> new IllegalStateException("No escrow account for deal"));

        // SM: → REFUNDING → REFUNDED_TO_DEPOSITOR
        EscrowAccountStateMachine.validate(escrowAccount.getStatus(), EscrowAccountStatus.REFUNDING);
        escrowAccount.setStatus(EscrowAccountStatus.REFUNDING);
        escrowAccountRepository.save(escrowAccount);

        // Double-entry: SYSTEM ESCROW_HOLDING → depositor AVAILABLE
        LedgerAccount depositorAvailable = ledgerService.getOrCreateAvailableAccount(depositorId, "RUB");
        UUID txId = UUID.randomUUID();
        ledgerService.transfer(ledgerService.getSystemEscrowAccountId(), depositorAvailable.getId(), hold.getAmount(), txId);

        escrowAccount.setStatus(EscrowAccountStatus.REFUNDED_TO_DEPOSITOR);
        escrowAccount.setRefundedAt(LocalDateTime.now());
        escrowAccountRepository.save(escrowAccount);

        // Legacy
        Account depositor = getOrCreateAccountEntity(depositorId);
        depositor.setBalance(depositor.getBalance().add(hold.getAmount()));
        accountRepository.save(depositor);

        hold.setStatus(EscrowHoldStatus.REFUNDED);
        hold.setReleasedAt(LocalDateTime.now());
        escrowHoldRepository.save(hold);

        Transaction tx = new Transaction();
        tx.setDealId(dealId);
        tx.setToAccountId(depositor.getId());
        tx.setAmount(hold.getAmount());
        tx.setType(TransactionType.REFUND_TO_DEPOSITOR);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setCompletedAt(LocalDateTime.now());
        transactionRepository.save(tx);

        saveOutboxEvent("EscrowAccount", dealId, "REFUNDED_TO_DEPOSITOR",
                Map.of("dealId", dealId, "depositorId", depositorId, "amount", hold.getAmount()));

        if (idempotencyKey != null) saveIdempotencyKey(idempotencyKey, "REFUNDED");
        log.info("Funds refunded (double-entry): dealId={}, depositorId={}", dealId, depositorId);
    }

    @Transactional
    public void refundFunds(UUID dealId, UUID depositorId) {
        refundFunds(dealId, depositorId, null);
    }

    @Transactional
    public void markDisputed(UUID dealId) {
        EscrowAccount ea = escrowAccountRepository.findByDealId(dealId).orElse(null);
        if (ea == null || ea.getStatus() != EscrowAccountStatus.FUNDS_SECURED) return;
        EscrowAccountStateMachine.validate(ea.getStatus(), EscrowAccountStatus.DISPUTED);
        ea.setStatus(EscrowAccountStatus.DISPUTED);
        escrowAccountRepository.save(ea);
        saveOutboxEvent("EscrowAccount", dealId, "DISPUTED", Map.of("dealId", dealId));
    }

    // ── Helpers ──────────────────────────────────────────────────

    private Account getOrCreateAccountEntity(UUID userId) {
        return accountRepository.findByUserIdForUpdate(userId)
                .orElseGet(() -> {
                    Account a = new Account();
                    a.setUserId(userId);
                    return accountRepository.save(a);
                });
    }

    private void saveOutboxEvent(String aggregateType, UUID aggregateId, String eventType, Map<String, Object> data) {
        OutboxEvent event = new OutboxEvent();
        event.setAggregateType(aggregateType);
        event.setAggregateId(aggregateId);
        event.setEventType(eventType);
        try {
            event.setPayload(objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            event.setPayload("{}");
        }
        outboxEventRepository.save(event);
    }

    private void saveIdempotencyKey(String key, String result) {
        IdempotencyKey ik = new IdempotencyKey();
        ik.setKey(key);
        ik.setResult(result);
        idempotencyKeyRepository.save(ik);
    }
}
