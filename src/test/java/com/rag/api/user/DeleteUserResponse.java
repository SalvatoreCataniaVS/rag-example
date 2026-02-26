package com.rag.api.user;

import com.rag.api.AbstractResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteUserResponse extends AbstractResponse {

    private Boolean completed;

}
