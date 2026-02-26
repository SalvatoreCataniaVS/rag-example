package com.rag.service;

import com.rag.api.user.*;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService {

    public CreateUserResponse createUser(CreateUserRequest request) {
        CreateUserResponse response = new CreateUserResponse();
        return response;
    }

    public RetrieveAllUsersResponse retrieveAllUsers() {
        RetrieveAllUsersResponse response = new RetrieveAllUsersResponse();
        return response;
    }

    public RetrieveUserResponse retrieveUser(String userId) {
        RetrieveUserResponse response = new RetrieveUserResponse();
        return response;
    }

    public DeleteUserResponse deleteUser(String userId) {
        DeleteUserResponse response = new DeleteUserResponse();
        return response;
    }

}
