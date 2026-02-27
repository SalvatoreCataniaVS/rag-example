package com.rag.service;

import com.rag.api.user.*;
import com.rag.mapper.UserMapper;
import com.rag.repository.UserRepository;
import com.rag.repository.entity.User;
import com.rag.validator.UserValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository repository;

    @Inject
    UserMapper userMapper;

    @Inject
    UserValidator userValidator;

    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        // Validate email uniqueness
        userValidator.validateEmailNotTaken(request.getUser().getMail());

        // Map request to entity and persist
        User user = userMapper.toEntity(request);
        repository.persist(user);

        // Map entity to response
        return userMapper.toCreateResponse(user);
    }

    public RetrieveAllUsersResponse retrieveAllUsers() {
        // Retrieve all users from DB
        List<User> users = repository.listAll();

        // Map entity list to response
        return userMapper.toRetrieveAllUsersResponse(users);
    }

    @Transactional
    public UpdateUserResponse updateUser(UUID userId, UpdateUserRequest request) {
        // Find user
        User user = userValidator.findOrThrow(userId);

        // Validate email uniqueness only if mail is actually changing
        userValidator.validateEmailNotTakenByOther(user, request.getUser().getMail());

        // Update fields
        user.setName(request.getUser().getName());
        user.setEmail(request.getUser().getMail());
        user.setAvatarUrl(request.getUser().getAvatarUrl());

        // Map entity to response
        return userMapper.toUpdateResponse(user);
    }

    public RetrieveUserResponse retrieveUser(UUID userId) {
        // Find user
        User user = userValidator.findOrThrow(userId);

        // Map entity to response
        return userMapper.toRetrieveUserResponse(user);
    }

    public UserSearchResponse searchUsers(UserSearchRequest request) {
        // Retrieve filtered users and total count for pagination
        List<User> users = repository.search(request);
        long totalElements = repository.countSearch(request);
        int totalPages = (int) Math.ceil((double) totalElements / request.getSize());

        // Map entity list to response
        return userMapper.toUserSearchResponse(request, users, totalElements, totalPages);
    }

    @Transactional
    public DeleteUserResponse deleteUser(UUID userId) {
        // Find user
        User user = userValidator.findOrThrow(userId);

        // Soft delete — preserve referential integrity with tasks, documents and chat history
        user.setActive(false);

        // Map entity to response
        return userMapper.toDeleteResponse(user);
    }

}