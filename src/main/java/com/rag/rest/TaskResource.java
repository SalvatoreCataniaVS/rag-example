package com.rag.rest;

import com.rag.api.task.*;
import com.rag.service.TaskService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/task")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    @Inject
    TaskService service;

    @POST
    @Authenticated
    public Response createTask(@Valid CreateTaskRequest request) {
        // TODO: replace hardcoded UUID with authenticated user id once security is active
        UUID createdBy = UUID.fromString("00000000-0000-0000-0000-000000000001");
        CreateTaskResponse response = service.createTask(request, createdBy);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Authenticated
    public Response retrieveAllTasks() {
        RetrieveAllTasksResponse response = service.retrieveAllTasks();
        return Response.ok(response).build();
    }

    @GET
    @Path("/{taskId}")
    @Authenticated
    public Response retrieveTask(@PathParam("taskId") UUID taskId) {
        RetrieveTaskResponse response = service.retrieveTask(taskId);
        return Response.ok(response).build();
    }

    @POST
    @Path("/search")
    @Authenticated
    public Response searchTasks(@Valid TaskSearchRequest request) {
        TaskSearchResponse response = service.searchTasks(request);
        return Response.ok(response).build();
    }

    @PUT
    @Path("/{taskId}")
    @Authenticated
    public Response updateTask(@PathParam("taskId") UUID taskId, @Valid UpdateTaskRequest request) {
        // TODO: replace hardcoded boolean with role check once security is active
        boolean isAdmin = true;
        UpdateTaskResponse response = service.updateTask(taskId, request, isAdmin);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{taskId}")
    @RolesAllowed("admin")
    public Response deleteTask(@PathParam("taskId") UUID taskId) {
        DeleteTaskResponse response = service.deleteTask(taskId);
        return Response.ok(response).build();
    }
    
}