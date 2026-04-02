package com.escrow.document.controller;

import com.escrow.document.entity.Document;
import com.escrow.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam UUID dealId,
            @RequestParam(required = false) String documentType,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Id") UUID userId) {
        try {
            Document doc = documentService.uploadDocument(dealId, userId, documentType, file);
            return ResponseEntity.ok(Map.of(
                    "id", doc.getId(),
                    "fileName", doc.getFileName(),
                    "contentType", doc.getContentType(),
                    "fileSize", doc.getFileSize(),
                    "documentType", doc.getDocumentType(),
                    "createdAt", doc.getCreatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/deal/{dealId}")
    public ResponseEntity<List<Document>> getDocumentsByDeal(@PathVariable UUID dealId) {
        return ResponseEntity.ok(documentService.getDocumentsByDeal(dealId));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable UUID id) {
        Document doc = documentService.getDocument(id);
        byte[] content = documentService.downloadDocument(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .body(content);
    }
}
