package com.escrow.escrow_account_service.api;

import com.escrow.escrow_account_service.domain.EscrowAccount;
import com.escrow.escrow_account_service.domain.EscrowAccountState;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class EscrowAccountDtos {

    public record OpenAccountRequest(
            @NotNull UUID dealId,
            @NotNull @DecimalMin("0.01") BigDecimal amount,
            @NotBlank String currency) {
    }

    public record EscrowAccountResponse(UUID id, UUID dealId, BigDecimal amount, String currency, EscrowAccountState state) {
        public static EscrowAccountResponse from(EscrowAccount account) {
            return new EscrowAccountResponse(account.getId(), account.getDealId(), account.getAmount(), account.getCurrency(), account.getState());
        }
    }
}
