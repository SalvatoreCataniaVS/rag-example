package com.rag.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.api.predict.ChatSourceChunk;
import com.rag.api.predict.ConversationDetailResponse;
import com.rag.api.predict.ConversationSummaryResponse;
import com.rag.common.exception.NotFoundException;
import com.rag.repository.ConversationRepository;
import com.rag.repository.entity.Conversation;
import com.rag.repository.entity.ConversationMessage;
import com.rag.repository.entity.MessageRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ConversationService {

    @Inject
    ConversationRepository conversationRepository;

    @Inject
    ObjectMapper objectMapper;

    public ConversationSummaryResponse listConversations(UUID userId) {
        // Retrieve all conversations for the user ordered by most recent
        List<Conversation> conversations = conversationRepository.findByUserId(userId);

        // Map entity list to response
        List<ConversationSummaryResponse.ConversationSummary> summaries = conversations.stream()
                .map(c -> ConversationSummaryResponse.ConversationSummary.builder()
                        .id(c.getId())
                        .title(c.getTitle())
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .build())
                .toList();

        return ConversationSummaryResponse.builder()
                .conversations(summaries)
                .build();
    }

    public ConversationDetailResponse getConversation(UUID conversationId) {
        // Find conversation
        Conversation conversation = conversationRepository.findByIdOptional(conversationId)
                .orElseThrow(() -> new NotFoundException("Conversation not found: " + conversationId));

        // Group messages into turns
        List<ConversationDetailResponse.ConversationTurn> turns = buildTurns(conversation.getMessages());

        // Map entity to response
        return ConversationDetailResponse.builder()
                .conversationId(conversation.getId())
                .title(conversation.getTitle())
                .createdAt(conversation.getCreatedAt())
                .turns(turns)
                .build();
    }

    // ─── Private ──────────────────────────────────────────────────────────────

    // Pairs USER and ASSISTANT messages into turns sequentially
    private List<ConversationDetailResponse.ConversationTurn> buildTurns(List<ConversationMessage> messages) {
        List<ConversationDetailResponse.ConversationTurn> turns = new ArrayList<>();

        // Messages are ordered by createdAt ASC — iterate two at a time (USER + ASSISTANT)
        for (int i = 0; i < messages.size() - 1; i += 2) {
            ConversationMessage userMsg = messages.get(i);
            ConversationMessage assistantMsg = messages.get(i + 1);

            // Safety check — skip malformed pairs
            if (userMsg.getRole() != MessageRole.USER || assistantMsg.getRole() != MessageRole.ASSISTANT) {
                continue;
            }

            // Deserialize sources and map to turn
            List<ChatSourceChunk> sources = deserializeSources(assistantMsg.getSources());
            turns.add(ConversationDetailResponse.ConversationTurn.builder()
                    .question(userMsg.getContent())
                    .answer(assistantMsg.getContent())
                    .sources(sources)
                    .askedAt(userMsg.getCreatedAt())
                    .build());
        }

        return turns;
    }

    // Deserializes the JSON sources stored on the assistant message
    private List<ChatSourceChunk> deserializeSources(String sourcesJson) {
        if (sourcesJson == null || sourcesJson.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(sourcesJson, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

}