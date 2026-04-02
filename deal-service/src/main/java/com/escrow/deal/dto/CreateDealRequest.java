package com.escrow.deal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateDealRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    private String currency;

    @NotNull
    private UUID beneficiaryId;
}
