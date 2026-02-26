package com.rag.api.user;

import com.rag.api.AbstractRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest extends AbstractRequest {

    private UserDTO user;

}
