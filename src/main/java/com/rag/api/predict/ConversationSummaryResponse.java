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
public class ConversationSummaryResponse {

    private List<ConversationSummary> conversations;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConversationSummary {
        private UUID id;
        private String title;
        private Instant createdAt;
        private Instant updatedAt;
    }

}