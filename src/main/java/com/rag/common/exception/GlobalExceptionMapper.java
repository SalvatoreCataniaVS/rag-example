package com.rag.common.exception;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.Instant;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(RuntimeException ex) {

        if (ex instanceof UserNotFoundException) {
            return build(Response.Status.NOT_FOUND, ex.getMessage());
        }

        if (ex instanceof NotFoundException) {
            return build(Response.Status.NOT_FOUND, ex.getMessage());
        }

        if (ex instanceof ConflictException) {
            return build(Response.Status.CONFLICT, ex.getMessage());
        }

        if (ex instanceof ForbiddenException) {
            return build(Response.Status.FORBIDDEN, ex.getMessage());
        }

        if (ex instanceof BadRequestException) {
            return build(Response.Status.BAD_REQUEST, ex.getMessage());
        }

        if (ex instanceof InternalServerException) {
            return build(Response.Status.INTERNAL_SERVER_ERROR, ex.getMessage());
        }

        // Fallback for any other unhandled runtime exception
        return build(Response.Status.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private Response build(Response.Status status, String message) {
        return Response.status(status)
                .entity(ErrorResponse.builder()
                        .status(status.getStatusCode())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .path(uriInfo.getPath())
                        .timestamp(Instant.now())
                        .build())
                .build();
    }

}