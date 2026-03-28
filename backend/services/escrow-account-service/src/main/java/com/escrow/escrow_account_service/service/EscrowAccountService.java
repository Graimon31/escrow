package com.escrow.escrow_account_service.service;

import com.escrow.escrow_account_service.domain.EscrowAccount;
import com.escrow.escrow_account_service.domain.EscrowAccountRepository;
import com.escrow.escrow_account_service.domain.EscrowAccountState;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class EscrowAccountService {

    private final EscrowAccountRepository repository;

    public EscrowAccountService(EscrowAccountRepository repository) {
        this.repository = repository;
    }

    public EscrowAccount open(UUID dealId, BigDecimal amount, String currency) {
        repository.findByDealId(dealId).ifPresent(existing -> {
            throw new IllegalStateException("Счёт эскроу для сделки уже открыт");
        });

        EscrowAccount account = new EscrowAccount();
        account.setDealId(dealId);
        account.setAmount(amount);
        account.setCurrency(currency);
        account.setState(EscrowAccountState.OPENED);
        account = repository.save(account);
        account.setState(EscrowAccountState.AWAITING_DEPOSIT);
        return repository.save(account);
    }

    public EscrowAccount byDeal(UUID dealId) {
        return repository.findByDealId(dealId).orElseThrow(() -> new IllegalArgumentException("Счёт эскроу не найден"));
    }
}
