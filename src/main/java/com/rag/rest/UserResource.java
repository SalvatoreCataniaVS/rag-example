package com.rag.rest;

import com.rag.api.user.*;
import com.rag.service.UserService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService service;

    @POST
    @RolesAllowed("admin")
    public Response createUser(@Valid CreateUserRequest request) {
        CreateUserResponse response = service.createUser(request);
        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @GET
    @Authenticated
    public Response retrieveAllUsers() {
        RetrieveAllUsersResponse response = service.retrieveAllUsers();
        return Response.ok(response).build();
    }

    @GET
    @Path("/{userId}")
    @Authenticated
    public Response retrieveUser(@PathParam("userId") UUID userId) {
        RetrieveUserResponse response = service.retrieveUser(userId);
        return Response.ok(response).build();
    }

    @POST
    @Path("/search")
    @Authenticated
    public Response searchUsers(@Valid UserSearchRequest request) {
        UserSearchResponse response = service.searchUsers(request);
        return Response.ok(response).build();
    }

    @PUT
    @Path("/{userId}")
    @Authenticated
    public Response updateUser(@PathParam("userId") UUID userId, @Valid UpdateUserRequest request) {
        UpdateUserResponse response = service.updateUser(userId, request);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{userId}")
    @RolesAllowed("admin")
    public Response deleteUser(@PathParam("userId") UUID userId) {
        DeleteUserResponse response = service.deleteUser(userId);
        return Response.ok(response).build();
    }

}