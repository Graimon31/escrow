package com.escrow.payment.service;

import com.escrow.payment.entity.LedgerAccount;
import com.escrow.payment.entity.LedgerEntry;
import com.escrow.payment.repository.LedgerAccountRepository;
import com.escrow.payment.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Core double-entry ledger engine.
 * Every financial operation creates exactly TWO entries: one DEBIT and one CREDIT.
 * Sum of all DEBITs must equal sum of all CREDITs (balanced books).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerService {

    private static final UUID SYSTEM_ESCROW_ACCOUNT_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final LedgerAccountRepository ledgerAccountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    /**
     * Get or create a user's AVAILABLE ledger account.
     */
    public LedgerAccount getOrCreateAvailableAccount(UUID userId, String currency) {
        return ledgerAccountRepository.findByOwnerIdAndAccountTypeAndCurrency(userId, "AVAILABLE", currency)
                .orElseGet(() -> {
                    LedgerAccount la = new LedgerAccount();
                    la.setOwnerId(userId);
                    la.setOwnerType("USER");
                    la.setAccountType("AVAILABLE");
                    la.setCurrency(currency);
                    la.setBalance(new BigDecimal("10000.00")); // Mock starting balance
                    return ledgerAccountRepository.save(la);
                });
    }

    /**
     * Transfer funds between two ledger accounts with double-entry.
     * DEBIT = money leaves the source account.
     * CREDIT = money enters the destination account.
     *
     * @return transaction UUID linking both entries
     */
    public UUID transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount, UUID txId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        // Lock accounts in consistent order to prevent deadlocks
        UUID first = fromAccountId.compareTo(toAccountId) < 0 ? fromAccountId : toAccountId;
        UUID second = fromAccountId.compareTo(toAccountId) < 0 ? toAccountId : fromAccountId;

        LedgerAccount firstAccount = ledgerAccountRepository.findByIdForUpdate(first)
                .orElseThrow(() -> new IllegalArgumentException("Ledger account not found: " + first));
        LedgerAccount secondAccount = ledgerAccountRepository.findByIdForUpdate(second)
                .orElseThrow(() -> new IllegalArgumentException("Ledger account not found: " + second));

        LedgerAccount source = fromAccountId.equals(first) ? firstAccount : secondAccount;
        LedgerAccount dest = toAccountId.equals(first) ? firstAccount : secondAccount;

        // Check sufficient balance
        if (source.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds in account " + fromAccountId
                    + ": balance=" + source.getBalance() + ", required=" + amount);
        }

        // DEBIT source (money leaves)
        source.setBalance(source.getBalance().subtract(amount));
        ledgerAccountRepository.save(source);

        LedgerEntry debitEntry = new LedgerEntry();
        debitEntry.setTransactionId(txId);
        debitEntry.setAccountId(fromAccountId);
        debitEntry.setEntryType("DEBIT");
        debitEntry.setAmount(amount);
        debitEntry.setBalanceAfter(source.getBalance());
        ledgerEntryRepository.save(debitEntry);

        // CREDIT destination (money enters)
        dest.setBalance(dest.getBalance().add(amount));
        ledgerAccountRepository.save(dest);

        LedgerEntry creditEntry = new LedgerEntry();
        creditEntry.setTransactionId(txId);
        creditEntry.setAccountId(toAccountId);
        creditEntry.setEntryType("CREDIT");
        creditEntry.setAmount(amount);
        creditEntry.setBalanceAfter(dest.getBalance());
        ledgerEntryRepository.save(creditEntry);

        log.info("Ledger transfer: {} → {}, amount={}, txId={}",
                fromAccountId, toAccountId, amount, txId);

        return txId;
    }

    public LedgerAccount getSystemEscrowAccount() {
        return ledgerAccountRepository.findById(SYSTEM_ESCROW_ACCOUNT_ID)
                .orElseThrow(() -> new IllegalStateException("System escrow account not found"));
    }

    public UUID getSystemEscrowAccountId() {
        return SYSTEM_ESCROW_ACCOUNT_ID;
    }
}
