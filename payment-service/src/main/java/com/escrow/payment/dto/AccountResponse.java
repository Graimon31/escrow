package com.escrow.payment.dto;

import com.escrow.payment.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AccountResponse {
    private UUID id;
    private UUID userId;
    private BigDecimal balance;
    private String currency;

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getUserId(),
                account.getBalance(), account.getCurrency());
    }
}
