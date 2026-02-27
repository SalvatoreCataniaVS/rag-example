package com.rag.common.exception;

public class InternalServerException extends RuntimeException {

    public InternalServerException() {
        super("An unexpected error occurred");
    }

    public InternalServerException(String message) {
        super(message);
    }
}