package com.rag.api.document;

import com.rag.repository.entity.DocumentStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UploadDocumentResponse {

    private UUID documentId;
    private String fileName;
    private DocumentStatus status;

}