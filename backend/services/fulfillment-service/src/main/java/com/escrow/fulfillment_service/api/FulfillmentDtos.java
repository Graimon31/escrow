package com.escrow.fulfillment_service.api;

import com.escrow.fulfillment_service.domain.FulfillmentDocumentMetadata;
import com.escrow.fulfillment_service.domain.FulfillmentRecord;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class FulfillmentDtos {

    public record DocumentMetaRequest(@NotBlank String fileName, @NotBlank String contentType, @NotNull Long sizeBytes) {}

    public record SubmitFulfillmentRequest(@NotNull UUID dealId,
                                           @NotBlank String description,
                                           @Valid @NotEmpty List<DocumentMetaRequest> documents) {
    }

    public record FulfillmentResponse(UUID id, UUID dealId, String beneficiaryUsername, String description, String status) {
        public static FulfillmentResponse from(FulfillmentRecord record) {
            return new FulfillmentResponse(record.getId(), record.getDealId(), record.getBeneficiaryUsername(), record.getDescription(), record.getStatus());
        }
    }

    public record DocumentMetaResponse(UUID id, UUID dealId, String fileName, String contentType, Long sizeBytes, String storagePath) {
        public static DocumentMetaResponse from(FulfillmentDocumentMetadata m) {
            return new DocumentMetaResponse(m.getId(), m.getDealId(), m.getFileName(), m.getContentType(), m.getSizeBytes(), m.getStoragePath());
        }
    }
}
