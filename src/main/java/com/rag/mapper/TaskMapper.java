package com.rag.mapper;

import com.rag.api.task.*;
import com.rag.repository.entity.Task;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TaskMapper {

    // Maps CreateTaskRequest to Task entity
    public Task toEntity(CreateTaskRequest request, UUID createdBy) {
        return Task.builder()
                .title(request.getTask().getTitle())
                .description(request.getTask().getDescription())
                .status(request.getTask().getStatus())
                .priority(request.getTask().getPriority())
                .documentId(request.getTask().getDocumentId())
                .dueDate(request.getTask().getDueDate())
                .createdBy(createdBy)
                .build();
    }

    // Maps Task entity to TaskDTO
    public TaskDTO toTaskDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setAssignedTo(task.getAssignedTo());
        dto.setDocumentId(task.getDocumentId());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedBy(task.getCreatedBy());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }

    // Maps Task entity to CreateTaskResponse
    public CreateTaskResponse toCreateResponse(Task task) {
        CreateTaskResponse response = new CreateTaskResponse();
        response.setTaskId(task.getId());
        return response;
    }

    // Maps Task entity to RetrieveTaskResponse
    public RetrieveTaskResponse toRetrieveResponse(Task task) {
        RetrieveTaskResponse response = new RetrieveTaskResponse();
        response.setTask(toTaskDTO(task));
        return response;
    }

    // Maps Task entity list to RetrieveAllTasksResponse
    public RetrieveAllTasksResponse toRetrieveAllResponse(List<Task> tasks) {
        RetrieveAllTasksResponse response = new RetrieveAllTasksResponse();
        response.setTaskList(tasks.stream().map(this::toTaskDTO).toList());
        return response;
    }

    // Maps Task entity to UpdateTaskResponse
    public UpdateTaskResponse toUpdateResponse(Task task) {
        UpdateTaskResponse response = new UpdateTaskResponse();
        response.setTaskId(task.getId());
        return response;
    }

    // Maps Task entity to DeleteTaskResponse
    public DeleteTaskResponse toDeleteResponse(Task task) {
        DeleteTaskResponse response = new DeleteTaskResponse();
        response.setTaskId(task.getId());
        return response;
    }

    // Maps Task entity list to TaskSearchResponse
    public TaskSearchResponse toSearchResponse(TaskSearchRequest request, List<Task> tasks, long totalElements, int totalPages) {
        TaskSearchResponse response = new TaskSearchResponse();
        response.setTasks(tasks.stream().map(this::toTaskDTO).toList());
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        return response;
    }

}