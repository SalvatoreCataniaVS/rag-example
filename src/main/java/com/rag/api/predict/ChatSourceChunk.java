package com.rag.api.predict;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSourceChunk {

    private UUID documentId;
    private String content;
    private double score;

}