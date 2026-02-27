package com.rag.repository;

import com.rag.api.user.UserSearchRequest;
import com.rag.repository.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, UUID> {

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }

    public List<User> findByTenantId(UUID tenantId) {
        return find("tenantId", tenantId).list();
    }

    // Dynamic search with filters and pagination
    public List<User> search(UserSearchRequest request) {
        StringBuilder query = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        applyFilters(query, params, request);

        return find(query.toString(), params)
                .page(request.getPage(), request.getSize())
                .list();
    }

    // Count total elements matching the filters (needed for totalPages)
    public long countSearch(UserSearchRequest request) {
        StringBuilder query = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        applyFilters(query, params, request);

        return count(query.toString(), params);
    }

    // Builds the dynamic query applying only non-null filters
    private void applyFilters(StringBuilder query, Map<String, Object> params, UserSearchRequest request) {
        if (request.getRole() != null) {
            query.append(" AND role = :role");
            params.put("role", request.getRole());
        }

        if (request.getActive() != null) {
            query.append(" AND active = :active");
            params.put("active", request.getActive());
        }

        if (request.getSearch() != null && !request.getSearch().isBlank()) {
            query.append(" AND (LOWER(name) LIKE :search OR LOWER(email) LIKE :search)");
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