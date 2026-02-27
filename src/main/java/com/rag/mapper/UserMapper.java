package com.rag.mapper;

import com.rag.api.user.*;
import com.rag.repository.entity.User;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class UserMapper {

    public User toEntity(CreateUserRequest request) {
        return User.builder()
                .email(request.getUser().getMail())
                .name(request.getUser().getName())
                .role(request.getUser().getRole())
                .build();
    }

    public UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setMail(user.getEmail());
        userDTO.setAvatarUrl(user.getAvatarUrl());
        userDTO.setRole(user.getRole());
        userDTO.setActive(user.isActive());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setLastLoginAt(user.getLastLoginAt());
        return userDTO;
    }

    public RetrieveAllUsersResponse toRetrieveAllUsersResponse(List<User> users) {
        RetrieveAllUsersResponse response = new RetrieveAllUsersResponse();
        response.setUserList(
                users.stream()
                        .map(this::toUserDTO)
                        .toList()
        );
        return response;
    }

    public UserSearchResponse toUserSearchResponse(UserSearchRequest request, List<User> users, long totalElements, int totalPages) {
        UserSearchResponse response = new UserSearchResponse();

        response.setUsers(
                users.stream()
                        .map(this::toUserDTO)
                        .toList()
        );
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        return response;
    }

    public RetrieveUserResponse toRetrieveUserResponse(User user) {
        RetrieveUserResponse response = new RetrieveUserResponse();
        response.setUser(toUserDTO(user));
        return response;
    }

    public CreateUserResponse toCreateResponse(User user) {
        CreateUserResponse response = new CreateUserResponse();
        response.setUserId(user.getId());
        return response;
    }

    public UpdateUserResponse toUpdateResponse(User user) {
        UpdateUserResponse response = new UpdateUserResponse();
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setEmail(user.getEmail());
        return response;
    }

    public DeleteUserResponse toDeleteResponse(User user) {
        DeleteUserResponse response = new DeleteUserResponse();
        response.setUserId(user.getId());
        return response;
    }

}