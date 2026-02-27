package com.rag.api.user;

import com.rag.repository.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInputDTO {

    @NotBlank(message = "Name must not be empty")
    private String name;

    @Email(message = "Mail format not valid")
    @NotBlank(message = "Mail must not be empty")
    private String mail;

    @NotNull(message = "Role must not be null")
    private Role role;

}