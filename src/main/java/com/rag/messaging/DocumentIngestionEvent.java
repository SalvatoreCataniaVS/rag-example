package com.rag.messaging;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentIngestionEvent {

    private UUID documentId;
    private UUID taskId;
    private String storagePath;
    private String mimeType;
    private String fileName;

}