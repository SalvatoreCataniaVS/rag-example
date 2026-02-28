package com.rag.service;

import com.rag.api.task.*;
import com.rag.mapper.TaskMapper;
import com.rag.repository.TaskRepository;
import com.rag.repository.entity.Task;
import com.rag.validator.TaskValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TaskService {

    @Inject
    TaskRepository repository;

    @Inject
    TaskMapper taskMapper;

    @Inject
    TaskValidator taskValidator;

    @Transactional
    public CreateTaskResponse createTask(CreateTaskRequest request, UUID createdBy) {
        // Map request to entity and persist
        Task task = taskMapper.toEntity(request, createdBy);
        repository.persist(task);

        // Map entity to response
        return taskMapper.toCreateResponse(task);
    }

    public RetrieveAllTasksResponse retrieveAllTasks() {
        // Retrieve all tasks from DB
        List<Task> tasks = repository.listAll();

        // Map entity list to response
        return taskMapper.toRetrieveAllResponse(tasks);
    }

    public RetrieveTaskResponse retrieveTask(UUID taskId) {
        // Find task
        Task task = taskValidator.findOrThrow(taskId);

        // Map entity to response
        return taskMapper.toRetrieveResponse(task);
    }

    public TaskSearchResponse searchTasks(TaskSearchRequest request) {
        // Retrieve filtered tasks and total count for pagination
        List<Task> tasks = repository.search(request);
        long totalElements = repository.countSearch(request);
        int totalPages = (int) Math.ceil((double) totalElements / request.getSize());

        // Map entity list to response
        return taskMapper.toSearchResponse(request, tasks, totalElements, totalPages);
    }

    @Transactional
    public UpdateTaskResponse updateTask(UUID taskId, UpdateTaskRequest request, boolean isAdmin) {
        // Find task
        Task task = taskValidator.findOrThrow(taskId);

        // Update fields
        task.setTitle(request.getTask().getTitle());
        task.setDescription(request.getTask().getDescription());
        task.setStatus(request.getTask().getStatus());
        task.setPriority(request.getTask().getPriority());
        task.setDocumentId(request.getTask().getDocumentId());
        task.setDueDate(request.getTask().getDueDate());

        // assignedTo is ADMIN only — ignored if caller is not ADMIN
        if (isAdmin && request.getTask().getAssignedTo() != null) {
            task.setAssignedTo(request.getTask().getAssignedTo());
        }

        // Map entity to response
        return taskMapper.toUpdateResponse(task);
    }

    @Transactional
    public DeleteTaskResponse deleteTask(UUID taskId) {
        // Find task
        Task task = taskValidator.findOrThrow(taskId);

        // Hard delete — tasks have no critical referential integrity constraints
        repository.delete(task);

        // Map entity to response
        return taskMapper.toDeleteResponse(task);
    }

}