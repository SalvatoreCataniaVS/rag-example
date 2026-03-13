package com.rag.api.predict;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDetailResponse {

    private UUID conversationId;
    private String title;
    private Instant createdAt;
    private List<ConversationTurn> turns;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConversationTurn {
        private String question;
        private String answer;
        private List<ChatSourceChunk> sources;
        private Instant askedAt;
    }

}