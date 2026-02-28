package com.rag.repository;

import com.rag.api.task.TaskSearchRequest;
import com.rag.repository.entity.Task;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class TaskRepository implements PanacheRepositoryBase<Task, UUID> {

    // Dynamic search with filters and pagination
    public List<Task> search(TaskSearchRequest request) {
        StringBuilder query = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        applyFilters(query, params, request);

        return find(query.toString(), params)
                .page(request.getPage(), request.getSize())
                .list();
    }

    // Count total elements matching the filters (needed for totalPages)
    public long countSearch(TaskSearchRequest request) {
        StringBuilder query = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        applyFilters(query, params, request);

        return count(query.toString(), params);
    }

    // Builds the dynamic query applying only non-null filters
    private void applyFilters(StringBuilder query, Map<String, Object> params, TaskSearchRequest request) {
        if (request.getStatus() != null) {
            query.append(" AND status = :status");
            params.put("status", request.getStatus());
        }

        if (request.getPriority() != null) {
            query.append(" AND priority = :priority");
            params.put("priority", request.getPriority());
        }

        if (request.getAssignedTo() != null) {
            query.append(" AND assignedTo = :assignedTo");
            params.put("assignedTo", request.getAssignedTo());
        }

        if (request.getCreatedBy() != null) {
            query.append(" AND createdBy = :createdBy");
            params.put("createdBy", request.getCreatedBy());
        }

        if (request.getSearch() != null && !request.getSearch().isBlank()) {
            query.append(" AND (LOWER(title) LIKE :search OR LOWER(description) LIKE :search)");
            params.put("search", "%" + request.getSearch().toLowerCase() + "%");
        }

        if (request.getDueDateFrom() != null) {
            query.append(" AND dueDate >= :dueDateFrom");
            params.put("dueDateFrom", request.getDueDateFrom());
        }

        if (request.getDueDateTo() != null) {
            query.append(" AND dueDate <= :dueDateTo");
            params.put("dueDateTo", request.getDueDateTo());
        }
    }
}