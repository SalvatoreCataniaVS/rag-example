package com.rag.validator;

import com.rag.common.exception.NotFoundException;
import com.rag.repository.TaskRepository;
import com.rag.repository.entity.Task;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class TaskValidator {

    @Inject
    TaskRepository repository;

    // Throws NotFoundException if task does not exist
    public Task findOrThrow(UUID taskId) {
        return repository.findByIdOptional(taskId)
                .orElseThrow(() -> new NotFoundException("Task with id " + taskId + " not found"));
    }

}