package com.rag.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.sql.DataSource;

@ApplicationScoped
public class AiConfig {

    @Inject
    DataSource dataSource;

    @ConfigProperty(name = "gemini.api.key")
    String geminiApiKey;

    @ConfigProperty(name = "gemini.embedding.model", defaultValue = "text-embedding-004")
    String embeddingModelName;

    @Produces
    @ApplicationScoped
    public EmbeddingModel embeddingModel() {
        return GoogleAiEmbeddingModel.builder()
                .apiKey(geminiApiKey)
                .modelName(embeddingModelName)
                .build();
    }

    @Produces
    @ApplicationScoped
    public EmbeddingStore<TextSegment> embeddingStore() {
        return PgVectorEmbeddingStore.datasourceBuilder()
                .datasource(dataSource)
                .table("document_chunks")
                .dimension(768)
                .createTable(false)
                .build();
    }
}