package com.rag.common.exception;

import java.util.UUID;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(UUID id) {
        super("User with id " + id + " not found");
    }

    public UserNotFoundException(String email) {
        super("User with email " + email + " not found");
    }
}