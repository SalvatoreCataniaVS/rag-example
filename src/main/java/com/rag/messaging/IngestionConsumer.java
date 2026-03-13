package com.rag.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.repository.DocumentRepository;
import com.rag.repository.entity.DocumentChunk;
import com.rag.repository.entity.DocumentStatus;
import com.rag.service.S3Service;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class IngestionConsumer {

    private static final Logger LOG = Logger.getLogger(IngestionConsumer.class);

    @Inject
    S3Service s3Service;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    EmbeddingModel embeddingModel;

    @Inject
    EmbeddingStore<TextSegment> embeddingStore;

    @Inject
    EntityManager entityManager;

    @Inject
    ObjectMapper objectMapper;

    @Incoming("document-ingestion-in")
    @Transactional
    public void consume(String payload) {
        DocumentIngestionEvent event;
        try {
            event = objectMapper.readValue(payload, DocumentIngestionEvent.class);
        } catch (Exception e) {
            LOG.errorf("Failed to deserialize ingestion event: %s", e.getMessage());
            return;
        }

        LOG.infof("Starting ingestion for document: %s", event.getDocumentId());

        // Mark document as PROCESSING
        documentRepository.findByIdOptional(event.getDocumentId()).ifPresent(doc -> {
            doc.setStatus(DocumentStatus.PROCESSING);
        });

        try {
            // 1. Download file from S3
            InputStream fileStream = s3Service.download(event.getStoragePath());

            // 2. Parse document based on mime type
            Document langchainDoc = parseDocument(fileStream, event.getMimeType());

            // 3. Split document into chunks — 500 tokens with 50 token overlap
            var splitter = DocumentSplitters.recursive(500, 50);
            List<TextSegment> segments = splitter.split(langchainDoc);

            LOG.infof("Document %s split into %d chunks", event.getDocumentId(), segments.size());

            // 4. Generate embeddings and store them in pgvector
            // Attach documentId to each segment's metadata before embedding
            for (TextSegment segment : segments) {
                segment.metadata().put("documentId", event.getDocumentId().toString());
            }
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
            embeddingStore.addAll(embeddings, segments);

            // 5. Persist chunk metadata to document_chunks for traceability
            for (int i = 0; i < segments.size(); i++) {
                DocumentChunk chunk = DocumentChunk.builder()
                        .id(UUID.randomUUID())
                        .documentId(event.getDocumentId())
                        .taskId(event.getTaskId())
                        .chunkIndex(i)
                        .content(segments.get(i).text())
                        .build();
                entityManager.persist(chunk);
            }

            // 6. Mark document as READY
            documentRepository.findByIdOptional(event.getDocumentId()).ifPresent(doc -> {
                doc.setStatus(DocumentStatus.READY);
            });

            LOG.infof("Ingestion completed for document: %s", event.getDocumentId());

        } catch (Exception e) {
            LOG.errorf("Ingestion failed for document %s: %s", event.getDocumentId(), e.getMessage());

            // Mark document as FAILED
            documentRepository.findByIdOptional(event.getDocumentId()).ifPresent(doc -> {
                doc.setStatus(DocumentStatus.FAILED);
            });
        }
    }

    // Select the appropriate parser based on the document mime type
    private Document parseDocument(InputStream stream, String mimeType) {
        DocumentParser parser = switch (mimeType) {
            case "application/pdf" -> new ApachePdfBoxDocumentParser();
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                 "application/msword" -> new ApachePoiDocumentParser();
            default -> new ApachePdfBoxDocumentParser(); // fallback
        };
        return parser.parse(stream);
    }

}