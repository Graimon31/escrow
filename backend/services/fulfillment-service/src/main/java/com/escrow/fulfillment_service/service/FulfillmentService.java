package com.escrow.fulfillment_service.service;

import com.escrow.fulfillment_service.api.FulfillmentDtos;
import com.escrow.fulfillment_service.domain.*;
import com.escrow.fulfillment_service.event.FulfillmentEvent;
import com.escrow.fulfillment_service.event.FulfillmentEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FulfillmentService {

    private final FulfillmentRepository repository;
    private final FulfillmentDocumentRepository documentRepository;
    private final FulfillmentEventPublisher eventPublisher;
    private final Path localStorageRoot;

    public FulfillmentService(FulfillmentRepository repository,
                              FulfillmentDocumentRepository documentRepository,
                              FulfillmentEventPublisher eventPublisher,
                              @Value("${app.storage.root:./data/documents}") String storageRoot) {
        this.repository = repository;
        this.documentRepository = documentRepository;
        this.eventPublisher = eventPublisher;
        this.localStorageRoot = Path.of(storageRoot);
    }

    @Transactional
    public FulfillmentRecord submit(UUID dealId, String beneficiary, String description, List<FulfillmentDtos.DocumentMetaRequest> docs) {
        var existing = repository.findByDealId(dealId);
        if (existing.isPresent()) {
            throw new IllegalStateException("Исполнение по сделке уже заявлено");
        }

        FulfillmentRecord record = new FulfillmentRecord();
        record.setDealId(dealId);
        record.setBeneficiaryUsername(beneficiary);
        record.setDescription(description);
        record.setStatus("SUBMITTED");
        record = repository.save(record);

        try {
            Files.createDirectories(localStorageRoot.resolve(dealId.toString()));
        } catch (Exception ignored) {
        }

        for (var d : docs) {
            FulfillmentDocumentMetadata metadata = new FulfillmentDocumentMetadata();
            metadata.setDealId(dealId);
            metadata.setFileName(d.fileName());
            metadata.setContentType(d.contentType());
            metadata.setSizeBytes(d.sizeBytes());
            String storagePath = localStorageRoot.resolve(dealId.toString()).resolve(d.fileName()).toString();
            metadata.setStoragePath(storagePath);
            documentRepository.save(metadata);
        }

        eventPublisher.publish(new FulfillmentEvent(
                dealId,
                "FULFILLMENT_SUBMITTED",
                beneficiary,
                OffsetDateTime.now(),
                description
        ));

        return record;
    }

    public FulfillmentRecord getByDeal(UUID dealId) {
        return repository.findByDealId(dealId).orElseThrow(() -> new IllegalArgumentException("Исполнение не найдено"));
    }

    public List<FulfillmentDocumentMetadata> documents(UUID dealId) {
        return documentRepository.findByDealId(dealId);
    }
}
