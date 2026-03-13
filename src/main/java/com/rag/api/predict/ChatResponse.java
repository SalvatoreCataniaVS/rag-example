package com.rag.api.predict;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {

    private UUID conversationId;
    private String answer;
    private List<ChatSourceChunk> sources;

}