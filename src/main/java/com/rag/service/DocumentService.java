package com.rag.service;

import com.rag.api.document.*;
import com.rag.common.exception.InternalServerException;
import com.rag.mapper.DocumentMapper;
import com.rag.messaging.DocumentIngestionEvent;
import com.rag.messaging.IngestionProducer;
import com.rag.repository.DocumentRepository;
import com.rag.repository.entity.Document;
import com.rag.repository.entity.DocumentStatus;
import com.rag.validator.DocumentValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DocumentService {

    @Inject
    DocumentRepository repository;

    @Inject
    DocumentMapper documentMapper;

    @Inject
    DocumentValidator documentValidator;

    @Inject
    S3Service s3Service;

    @Inject
    IngestionProducer ingestionProducer;

    @Transactional
    public UploadDocumentResponse upload(InputStream fileStream, String fileName, String mimeType, long fileSize, UUID uploadedBy, UUID taskId) {
        // Validate mime type
        documentValidator.validateMimeType(mimeType);

        // Save metadata to DB with UPLOADED status
        Document document = Document.builder()
                .fileName(fileName)
                .mimeType(mimeType)
                .fileSize(fileSize)
                .storagePath("")
                .status(DocumentStatus.UPLOADED)
                .taskId(taskId)
                .uploadedBy(uploadedBy)
                .build();
        repository.persist(document);

        // Upload file to MinIO
        try {
            document.setStatus(DocumentStatus.PROCESSING);
            String storagePath = s3Service.upload(fileStream, fileName, mimeType, fileSize);
            document.setStoragePath(storagePath);

            // Produce evento Kafka per la pipeline di ingestion
            ingestionProducer.send(DocumentIngestionEvent.builder()
                    .documentId(document.getId())
                    .taskId(document.getTaskId())
                    .storagePath(storagePath)
                    .mimeType(mimeType)
                    .fileName(fileName)
                    .build());

        } catch (Exception e) {
            document.setStatus(DocumentStatus.FAILED);
            throw new InternalServerException("Failed to upload document: " + e.getMessage());
        }

        // Map entity to response
        return documentMapper.toUploadResponse(document);
    }

    public RetrieveAllDocumentsResponse retrieveAllDocuments() {
        // Retrieve all documents from DB
        List<Document> documents = repository.listAll();

        // Map entity list to response
        return documentMapper.toRetrieveAllResponse(documents);
    }

    public RetrieveDocumentResponse retrieveDocument(UUID documentId) {
        // Find document
        Document document = documentValidator.findOrThrow(documentId);

        // Map entity to response
        return documentMapper.toRetrieveResponse(document);
    }

    public DocumentSearchResponse searchDocuments(DocumentSearchRequest request) {
        // Retrieve filtered documents and total count for pagination
        List<Document> documents = repository.search(request);
        long totalElements = repository.countSearch(request);
        int totalPages = (int) Math.ceil((double) totalElements / request.getSize());

        // Map entity list to response
        return documentMapper.toSearchResponse(request, documents, totalElements, totalPages);
    }

    public InputStream download(UUID documentId) {
        // Find document
        Document document = documentValidator.findOrThrow(documentId);

        // Download file from MinIO
        return s3Service.download(document.getStoragePath());
    }

    @Transactional
    public DeleteDocumentResponse deleteDocument(UUID documentId) {
        // Find document
        Document document = documentValidator.findOrThrow(documentId);

        // Delete file from MinIO
        s3Service.delete(document.getStoragePath());

        // Delete metadata from DB
        repository.delete(document);

        // Map entity to response
        return documentMapper.toDeleteResponse(document);
    }

}