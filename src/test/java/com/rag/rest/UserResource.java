package com.rag.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.api.user.*;
import com.rag.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user")
public class UserResource {

    @Inject
    private UserService service;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("")
    public Response createUser(CreateUserRequest request) throws JsonProcessingException {
        CreateUserResponse response = service.createUser(request);
        ObjectMapper objectMapper = new ObjectMapper();
        return Response.status(200)
                .entity(objectMapper.readValue(objectMapper.writeValueAsString(response), Object.class))
                .build();
    }

    @GET
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("")
    public Response retrieveAllUsers() throws JsonProcessingException {
        RetrieveAllUsersResponse response = service.retrieveAllUsers();
        ObjectMapper objectMapper = new ObjectMapper();
        return Response.status(200)
                .entity(objectMapper.readValue(objectMapper.writeValueAsString(response), Object.class))
                .build();
    }

    @GET
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{userId}")
    public Response retrieveUser(@PathParam("userId") String userId) throws JsonProcessingException {
        RetrieveUserResponse response = service.retrieveUser(userId);
        ObjectMapper objectMapper = new ObjectMapper();
        return Response.status(200)
                .entity(objectMapper.readValue(objectMapper.writeValueAsString(response), Object.class))
                .build();
    }

    @DELETE
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") String userId) throws JsonProcessingException {
        DeleteUserResponse response = service.deleteUser(userId);
        ObjectMapper objectMapper = new ObjectMapper();
        return Response.status(200)
                .entity(objectMapper.readValue(objectMapper.writeValueAsString(response), Object.class))
                .build();
    }

}
