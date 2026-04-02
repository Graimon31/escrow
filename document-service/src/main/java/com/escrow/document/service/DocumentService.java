package com.escrow.document.service;

import com.escrow.document.entity.Document;
import com.escrow.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private static final String STORAGE_DIR = "/app/uploads";

    private final DocumentRepository documentRepository;

    public Document uploadDocument(UUID dealId, UUID uploaderId, String documentType, MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String storageName = UUID.randomUUID() + "_" + fileName;
            Path storageDir = Paths.get(STORAGE_DIR, dealId.toString());
            Files.createDirectories(storageDir);
            Path filePath = storageDir.resolve(storageName);
            file.transferTo(filePath.toFile());

            Document doc = Document.builder()
                    .dealId(dealId)
                    .uploaderId(uploaderId)
                    .fileName(fileName)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .storagePath(filePath.toString())
                    .documentType(documentType != null ? documentType : "GENERAL")
                    .build();
            return documentRepository.save(doc);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    public List<Document> getDocumentsByDeal(UUID dealId) {
        return documentRepository.findByDealIdOrderByCreatedAtDesc(dealId);
    }

    public byte[] downloadDocument(UUID documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
        try {
            return Files.readAllBytes(Paths.get(doc.getStoragePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + e.getMessage(), e);
        }
    }

    public Document getDocument(UUID documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
    }
}
