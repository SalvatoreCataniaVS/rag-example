package com.rag.api.task;

import com.rag.repository.entity.TaskPriority;
import com.rag.repository.entity.TaskStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class TaskSearchRequest {

    @Min(value = 0, message = "Page must be >= 0")
    private int page = 0;

    @Min(value = 1, message = "Size must be >= 1")
    @Max(value = 100, message = "Size must be <= 100")
    private int size = 20;

    private TaskStatus status;
    private TaskPriority priority;
    private UUID assignedTo;
    private UUID createdBy;
    private String search;          // searches on title and description
    private LocalDate dueDateFrom;
    private LocalDate dueDateTo;
}
