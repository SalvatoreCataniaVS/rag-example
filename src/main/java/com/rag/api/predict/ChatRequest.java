package com.rag.api.predict;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {

    @NotBlank(message = "Question must not be blank")
    private String question;

    // If provided, the conversation is resumed — otherwise a new one is created
    private UUID conversationId;

    // Optional — scopes the search to documents belonging to this task
    private UUID taskId;

    // Number of chunks to retrieve — default 5, max 20
    @Min(1) @Max(20)
    @Builder.Default
    private int topK = 5;

}