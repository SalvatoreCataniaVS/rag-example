package com.rag.api.task;

import com.rag.repository.entity.TaskPriority;
import com.rag.repository.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class TaskInputDTO {

    @NotBlank(message = "Title must not be empty")
    private String title;

    private String description;

    @NotNull(message = "Status must not be null")
    private TaskStatus status;

    @NotNull(message = "Priority must not be null")
    private TaskPriority priority;

    private UUID assignedTo;    // ADMIN only — ignored if set by USER
    private UUID documentId;
    private LocalDate dueDate;
}