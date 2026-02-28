package com.rag.api.task;

import com.rag.repository.entity.TaskPriority;
import com.rag.repository.entity.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class TaskDTO {

    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private UUID assignedTo;
    private UUID documentId;
    private LocalDate dueDate;
    private UUID createdBy;
    private Instant createdAt;
    private Instant updatedAt;

}