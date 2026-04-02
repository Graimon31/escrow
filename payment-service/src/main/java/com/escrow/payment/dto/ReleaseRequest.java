package com.escrow.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReleaseRequest {

    @NotNull
    private UUID dealId;

    @NotNull
    private UUID beneficiaryId;
}
