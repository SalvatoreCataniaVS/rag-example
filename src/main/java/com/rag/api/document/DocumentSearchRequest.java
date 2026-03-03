package com.rag.api.document;

import com.rag.repository.entity.DocumentStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class DocumentSearchRequest {

    @Min(value = 0, message = "Page must be >= 0")
    private int page = 0;

    @Min(value = 1, message = "Size must be >= 1")
    @Max(value = 100, message = "Size must be <= 100")
    private int size = 20;

    private DocumentStatus status;
    private UUID uploadedBy;
    private UUID taskId;
    private String mimeType;
    private String search;          // searches on fileName
    private Instant createdFrom;
    private Instant createdTo;
}