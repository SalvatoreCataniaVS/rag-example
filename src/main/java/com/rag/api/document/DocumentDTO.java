package com.rag.api.document;

import com.rag.repository.entity.DocumentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class DocumentDTO {

    private UUID id;
    private String fileName;
    private String mimeType;
    private Long fileSize;
    private String storagePath;
    private DocumentStatus status;
    private UUID taskId;
    private UUID uploadedBy;
    private Instant createdAt;
    private Instant updatedAt;

}