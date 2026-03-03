package com.rag.repository;

import com.rag.api.document.DocumentSearchRequest;
import com.rag.repository.entity.Document;
import com.rag.repository.entity.DocumentStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class DocumentRepository implements PanacheRepositoryBase<Document, UUID> {

    public List<Document> findByUploadedBy(UUID uploadedBy) {
        return find("uploadedBy", uploadedBy).list();
    }

    public List<Document> findByStatus(DocumentStatus status) {
        return find("status", status).list();
    }

    public List<Document> findByTaskId(UUID taskId) {
        return find("taskId", taskId).list();
    }

    // Dynamic search with filters and pagination
    public List<Document> search(DocumentSearchRequest request) {
        StringBuilder query = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        applyFilters(query, params, request);

        return find(query.toString(), params)
                .page(request.getPage(), request.getSize())
                .list();
    }

    // Count total elements matching the filters (needed for totalPages)
    public long countSearch(DocumentSearchRequest request) {
        StringBuilder query = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        applyFilters(query, params, request);

        return count(query.toString(), params);
    }

    // Builds the dynamic query applying only non-null filters
    private void applyFilters(StringBuilder query, Map<String, Object> params, DocumentSearchRequest request) {
        if (request.getStatus() != null) {
            query.append(" AND status = :status");
            params.put("status", request.getStatus());
        }

        if (request.getUploadedBy() != null) {
            query.append(" AND uploadedBy = :uploadedBy");
            params.put("uploadedBy", request.getUploadedBy());
        }

        if (request.getTaskId() != null) {
            query.append(" AND taskId = :taskId");
            params.put("taskId", request.getTaskId());
        }

        if (request.getMimeType() != null && !request.getMimeType().isBlank()) {
            query.append(" AND mimeType = :mimeType");
            params.put("mimeType", request.getMimeType());
        }

        if (request.getSearch() != null && !request.getSearch().isBlank()) {
            query.append(" AND LOWER(fileName) LIKE :search");
            params.put("search", "%" + request.getSearch().toLowerCase() + "%");
        }

        if (request.getCreatedFrom() != null) {
            query.append(" AND createdAt >= :createdFrom");
            params.put("createdFrom", request.getCreatedFrom());
        }

        if (request.getCreatedTo() != null) {
            query.append(" AND createdAt <= :createdTo");
            params.put("createdTo", request.getCreatedTo());
        }
    }

}