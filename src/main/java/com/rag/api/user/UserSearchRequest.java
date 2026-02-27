package com.rag.api.user;

import com.rag.repository.entity.Role;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserSearchRequest {

    // Pagination
    @Min(value = 0, message = "Page must be >= 0")
    private int page = 0;

    @Min(value = 1, message = "Size must be >= 1")
    @Max(value = 100, message = "Size must be <= 100")
    private int size = 20;

    // Filters
    private Role role;
    private Boolean active;
    private String search;          // searches on name and mail (case-insensitive)
    private Instant createdFrom;    // range start (inclusive)
    private Instant createdTo;      // range end (inclusive)

}