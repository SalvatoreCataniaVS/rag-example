package com.rag.interceptor.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogEntry {

    // Common fields
    private final Instant timestamp;
    private final String phase;
    private final String method;
    private final String path;
    private final String payload;

    // Inbound only
    private final String userId;
    private final String sessionId;
    private final Boolean authenticated;
    private final String remoteAddress;

    // Response only
    private final Integer status;
    private final Long durationMs;

    // Outbound only
    private final String targetUrl;

}