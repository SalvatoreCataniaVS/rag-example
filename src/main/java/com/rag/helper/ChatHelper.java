package com.rag.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.api.predict.ChatSourceChunk;
import com.rag.repository.entity.Conversation;
import com.rag.repository.entity.ConversationMessage;
import com.rag.repository.entity.MessageRole;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ChatHelper {

    private static final Logger LOG = Logger.getLogger(ChatHelper.class);

    @Inject
    ObjectMapper objectMapper;

    public List<ChatMessage> buildMessageHistory(String systemPrompt,
                                                 Conversation conversation,
                                                 List<EmbeddingMatch<TextSegment>> matches,
                                                 String question) {
        List<ChatMessage> messages = new ArrayList<>();

        // Add system prompt
        messages.add(SystemMessage.from(systemPrompt));

        // Replay past conversation turns
        for (ConversationMessage past : conversation.getMessages()) {
            if (past.getRole() == MessageRole.USER) {
                messages.add(UserMessage.from(past.getContent()));
            } else {
                messages.add(AiMessage.from(past.getContent()));
            }
        }

        // Append current question with retrieved context
        messages.add(UserMessage.from(buildPromptWithContext(matches, question)));

        return messages;
    }

    public String buildPromptWithContext(List<EmbeddingMatch<TextSegment>> matches, String question) {
        // Return question as-is if no context is available
        if (matches.isEmpty()) {
            return question;
        }

        // Build context block from retrieved chunks
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < matches.size(); i++) {
            context.append("--- Source ").append(i + 1).append(" ---\n");
            context.append(matches.get(i).embedded().text()).append("\n\n");
        }

        return """
                Context:
                %s
                Question: %s
                """.formatted(context, question);
    }

    public List<ChatSourceChunk> buildSources(List<EmbeddingMatch<TextSegment>> matches) {
        // Map each embedding match to a ChatSourceChunk DTO
        return matches.stream()
                .map(match -> {
                    String docIdMeta = match.embedded().metadata().getString("documentId");
                    return ChatSourceChunk.builder()
                            .documentId(docIdMeta != null ? UUID.fromString(docIdMeta) : null)
                            .content(match.embedded().text())
                            .score(match.score())
                            .build();
                })
                .toList();
    }

    public void saveMessages(Conversation conversation, String question, String answer, List<ChatSourceChunk> sources) {
        // Persist user message
        ConversationMessage userMessage = ConversationMessage.builder()
                .conversation(conversation)
                .role(MessageRole.USER)
                .content(question)
                .build();
        conversation.getMessages().add(userMessage);

        // Serialize sources to JSON
        String sourcesJson = null;
        try {
            sourcesJson = objectMapper.writeValueAsString(sources);
        } catch (JsonProcessingException e) {
            LOG.warnf("Failed to serialize sources for conversation %s", conversation.getId());
        }

        // Persist assistant message with sources
        ConversationMessage assistantMessage = ConversationMessage.builder()
                .conversation(conversation)
                .role(MessageRole.ASSISTANT)
                .content(answer)
                .sources(sourcesJson)
                .build();
        conversation.getMessages().add(assistantMessage);
    }

    public String deriveTitle(String question) {
        // Truncate to 80 chars if the question exceeds the title limit
        return question.length() > 80
                ? question.substring(0, 80) + "..."
                : question;
    }

}