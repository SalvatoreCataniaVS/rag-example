package com.rag.rest;

import com.rag.service.TaskService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/task")
public class TaskResource {

    @Inject
    private TaskService service;

}
