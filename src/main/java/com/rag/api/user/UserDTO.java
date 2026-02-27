package com.rag.api.user;

import com.rag.repository.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserDTO {

    private UUID id;
    private String name;
    private String mail;
    private String avatarUrl;
    private Role role;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;

}