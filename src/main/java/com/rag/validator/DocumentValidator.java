package com.rag.validator;

import com.rag.common.exception.BadRequestException;
import com.rag.common.exception.NotFoundException;
import com.rag.repository.DocumentRepository;
import com.rag.repository.entity.Document;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class DocumentValidator {

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain",
            "text/csv",
            "text/markdown"
    );

    @Inject
    DocumentRepository repository;

    // Throws NotFoundException if document does not exist
    public Document findOrThrow(UUID documentId) {
        return repository.findByIdOptional(documentId)
                .orElseThrow(() -> new NotFoundException("Document with id " + documentId + " not found"));
    }

    // Throws BadRequestException if mime type is not supported
    public void validateMimeType(String mimeType) {
        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new BadRequestException("Unsupported file type: " + mimeType +
                    ". Allowed types: PDF, DOCX, TXT, CSV, Markdown");
        }
    }

}