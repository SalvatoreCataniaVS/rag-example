package com.rag.service;

import com.rag.api.predict.ChatRequest;
import com.rag.api.predict.ChatResponse;
import com.rag.api.predict.ChatSourceChunk;
import com.rag.common.exception.NotFoundException;
import com.rag.helper.ChatHelper;
import com.rag.repository.ConversationRepository;
import com.rag.repository.entity.Conversation;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PredictService {

    private static final String SYSTEM_PROMPT = """
            You are a helpful assistant that answers questions based strictly on the provided context.
            If the answer cannot be found in the context, say so clearly — do not make up information.
            Always be concise and precise.
            """;

    @Inject
    EmbeddingModel embeddingModel;

    @Inject
    EmbeddingStore<TextSegment> embeddingStore;

    @Inject
    ConversationRepository conversationRepository;

    @Inject
    ChatHelper chatHelper;

    @ConfigProperty(name = "gemini.api.key")
    String geminiApiKey;

    @ConfigProperty(name = "gemini.chat.model", defaultValue = "gemini-2.0-flash")
    String chatModelName;

    @Transactional
    public ChatResponse chat(ChatRequest request, UUID userId) {
        // Resolve or create conversation
        Conversation conversation = resolveConversation(request, userId);

        // Search for relevant chunks in pgvector
        List<EmbeddingMatch<TextSegment>> matches = searchChunks(request.getQuestion(), request.getTopK());
        List<ChatSourceChunk> sources = chatHelper.buildSources(matches);

        // Build full message history and call Gemini
        List<ChatMessage> messages = chatHelper.buildMessageHistory(
                SYSTEM_PROMPT, conversation, matches, request.getQuestion()
        );
        String answer = callGemini(messages);

        // Persist user message and assistant response
        chatHelper.saveMessages(conversation, request.getQuestion(), answer, sources);

        // Map to response
        return ChatResponse.builder()
                .conversationId(conversation.getId())
                .answer(answer)
                .sources(sources)
                .build();
    }

    public ChatResponse chatIncognito(ChatRequest request) {
        // Search for relevant chunks in pgvector
        List<EmbeddingMatch<TextSegment>> matches = searchChunks(request.getQuestion(), request.getTopK());

        // Return early if no relevant documents found
        if (matches.isEmpty()) {
            return ChatResponse.builder()
                    .answer("No relevant documents found to answer your question.")
                    .sources(List.of())
                    .build();
        }

        // Build single-turn prompt and call Gemini
        List<ChatMessage> messages = List.of(
                SystemMessage.from(SYSTEM_PROMPT),
                UserMessage.from(chatHelper.buildPromptWithContext(matches, request.getQuestion()))
        );

        // Map to response
        return ChatResponse.builder()
                .answer(callGemini(messages))
                .sources(chatHelper.buildSources(matches))
                .build();
    }

    // ─── Private ──────────────────────────────────────────────────────────────

    private Conversation resolveConversation(ChatRequest request, UUID userId) {
        if (request.getConversationId() != null) {
            // Resume existing conversation
            return conversationRepository.findByIdOptional(request.getConversationId())
                    .orElseThrow(() -> new NotFoundException("Conversation not found: " + request.getConversationId()));
        }

        // Create new conversation
        Conversation conversation = Conversation.builder()
                .userId(userId)
                .taskId(request.getTaskId())
                .title(chatHelper.deriveTitle(request.getQuestion()))
                .messages(new ArrayList<>())
                .build();
        conversationRepository.persist(conversation);
        return conversation;
    }

    private List<EmbeddingMatch<TextSegment>> searchChunks(String question, int topK) {
        Embedding questionEmbedding = embeddingModel.embed(question).content();

        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(questionEmbedding)
                .maxResults(topK)
                .minScore(0.5)
                .build();

        return embeddingStore.search(searchRequest).matches();
    }

    private String callGemini(List<ChatMessage> messages) {
        ChatLanguageModel chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName(chatModelName)
                .build();

        return chatModel.generate(messages).content().text();
    }

}