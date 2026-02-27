package com.rag.validator;

import com.rag.common.exception.ConflictException;
import com.rag.common.exception.UserNotFoundException;
import com.rag.repository.UserRepository;
import com.rag.repository.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class UserValidator {

    @Inject
    UserRepository repository;

    // Throws ConflictException if email is already in use
    public void validateEmailNotTaken(String mail) {
        if (repository.existsByEmail(mail)) {
            throw new ConflictException("Email " + mail + " is already in use");
        }
    }

    // Throws ConflictException if email is already in use by another user
    public void validateEmailNotTakenByOther(User user, String mail) {
        if (!user.getEmail().equals(mail) && repository.existsByEmail(mail)) {
            throw new ConflictException("Email " + mail + " is already in use");
        }
    }

    // Throws UserNotFoundException if user does not exist
    public User findOrThrow(UUID userId) {
        return repository.findByIdOptional(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

}