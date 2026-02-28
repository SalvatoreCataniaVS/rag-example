package com.rag.api.task;

import com.rag.common.request.AbstractRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaskRequest extends AbstractRequest {

    @Valid
    @NotNull(message = "Task object must not be null")
    private TaskInputDTO task;

}
