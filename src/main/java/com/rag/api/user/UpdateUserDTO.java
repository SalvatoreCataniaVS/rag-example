package com.rag.api.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDTO {

    @NotBlank(message = "Name must not be empty")
    private String name;

    @Email(message = "Mail format not valid")
    @NotBlank(message = "Mail must not be empty")
    private String mail;

    private String avatarUrl;

}