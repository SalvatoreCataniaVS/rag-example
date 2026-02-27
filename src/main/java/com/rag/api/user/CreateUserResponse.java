package com.rag.api.user;

import com.rag.common.request.AbstractResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateUserResponse extends AbstractResponse {

    private UUID userId;

}
