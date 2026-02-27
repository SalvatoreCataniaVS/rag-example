package com.rag.api.user;

import com.rag.common.request.AbstractRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest extends AbstractRequest {

    @Valid
    @NotNull(message = "User object must not be null")
    private UpdateUserDTO user;

}