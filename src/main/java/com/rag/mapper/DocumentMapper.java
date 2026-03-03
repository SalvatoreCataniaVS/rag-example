package com.rag.mapper;

import com.rag.api.document.*;
import com.rag.repository.entity.Document;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class DocumentMapper {

    // Maps Document entity to DocumentDTO
    public DocumentDTO toDocumentDTO(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setFileName(document.getFileName());
        dto.setMimeType(document.getMimeType());
        dto.setFileSize(document.getFileSize());
        dto.setStoragePath(document.getStoragePath());
        dto.setStatus(document.getStatus());
        dto.setTaskId(document.getTaskId());
        dto.setUploadedBy(document.getUploadedBy());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        return dto;
    }

    // Maps Document entity to UploadDocumentResponse
    public UploadDocumentResponse toUploadResponse(Document document) {
        UploadDocumentResponse response = new UploadDocumentResponse();
        response.setDocumentId(document.getId());
        response.setFileName(document.getFileName());
        response.setStatus(document.getStatus());
        return response;
    }

    // Maps Document entity to RetrieveDocumentResponse
    public RetrieveDocumentResponse toRetrieveResponse(Document document) {
        RetrieveDocumentResponse response = new RetrieveDocumentResponse();
        response.setDocument(toDocumentDTO(document));
        return response;
    }

    // Maps Document entity list to RetrieveAllDocumentsResponse
    public RetrieveAllDocumentsResponse toRetrieveAllResponse(List<Document> documents) {
        RetrieveAllDocumentsResponse response = new RetrieveAllDocumentsResponse();
        response.setDocumentList(documents.stream().map(this::toDocumentDTO).toList());
        return response;
    }

    // Maps Document entity list to DocumentSearchResponse
    public DocumentSearchResponse toSearchResponse(DocumentSearchRequest request, List<Document> documents, long totalElements, int totalPages) {
        DocumentSearchResponse response = new DocumentSearchResponse();
        response.setDocuments(documents.stream().map(this::toDocumentDTO).toList());
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        return response;
    }

    // Maps Document entity to DeleteDocumentResponse
    public DeleteDocumentResponse toDeleteResponse(Document document) {
        DeleteDocumentResponse response = new DeleteDocumentResponse();
        response.setDocumentId(document.getId());
        return response;
    }

}