package com.rag.api.user;

import com.rag.api.AbstractResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetrieveUserResponse extends AbstractResponse {

    private UserDTO user;

}
