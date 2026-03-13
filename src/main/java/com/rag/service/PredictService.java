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
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PredictService {

    private static final Logger LOG = Logger.getLogger(PredictService.class);

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

    // ─── Stateful chat — saves history to DB ─────────────────────────────────

    @Transactional
    public ChatResponse chat(ChatRequest request, UUID userId) {
        Conversation conversation = resolveConversation(request, userId);

        List<EmbeddingMatch<TextSegment>> matches = searchChunks(request.getQuestion(), request.getTopK());
        List<ChatSourceChunk> sources = chatHelper.buildSources(matches);

        List<ChatMessage> messages = chatHelper.buildMessageHistory(
                SYSTEM_PROMPT, conversation, matches, request.getQuestion()
        );

        String answer = callGemini(messages);

        chatHelper.saveMessages(conversation, request.getQuestion(), answer, sources);

        return ChatResponse.builder()
                .conversationId(conversation.getId())
                .answer(answer)
                .sources(sources)
                .build();
    }

    // ─── Stateless chat — no persistence ─────────────────────────────────────

    public ChatResponse chatIncognito(ChatRequest request) {
        List<EmbeddingMatch<TextSegment>> matches = searchChunks(request.getQuestion(), request.getTopK());

        if (matches.isEmpty()) {
            return ChatResponse.builder()
                    .answer("No relevant documents found to answer your question.")
                    .sources(List.of())
                    .build();
        }

        List<ChatMessage> messages = List.of(
                SystemMessage.from(SYSTEM_PROMPT),
                UserMessage.from(chatHelper.buildPromptWithContext(matches, request.getQuestion()))
        );

        return ChatResponse.builder()
                .answer(callGemini(messages))
                .sources(chatHelper.buildSources(matches))
                .build();
    }

    // ─── Private ──────────────────────────────────────────────────────────────

    private Conversation resolveConversation(ChatRequest request, UUID userId) {
        if (request.getConversationId() != null) {
            return conversationRepository.findByIdOptional(request.getConversationId())
                    .orElseThrow(() -> new NotFoundException("Conversation not found: " + request.getConversationId()));
        }

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