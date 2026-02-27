package com.rag.common.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;
    private Instant timestamp;

}