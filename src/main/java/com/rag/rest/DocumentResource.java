package com.rag.rest;

import com.rag.api.document.*;
import com.rag.service.DocumentService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.util.UUID;

@Path("/document")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentResource {

    @Inject
    DocumentService service;

    @POST
    @Path("/upload")
    @Authenticated
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(
            @RestForm("file") FileUpload file,
            @RestForm("taskId") UUID taskId) throws IOException {

        // TODO: replace hardcoded UUID with authenticated user id once security is active
        UUID uploadedBy = UUID.fromString("00000000-0000-0000-0000-000000000001");

        UploadDocumentResponse response = service.upload(
                file.uploadedFile().toFile().toURI().toURL().openStream(),
                file.fileName(),
                file.contentType(),
                file.size(),
                uploadedBy,
                taskId
        );

        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Authenticated
    public Response retrieveAllDocuments() {
        RetrieveAllDocumentsResponse response = service.retrieveAllDocuments();
        return Response.ok(response).build();
    }

    @GET
    @Path("/{documentId}")
    @Authenticated
    public Response retrieveDocument(@PathParam("documentId") UUID documentId) {
        RetrieveDocumentResponse response = service.retrieveDocument(documentId);
        return Response.ok(response).build();
    }

    @POST
    @Path("/search")
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    public Response searchDocuments(@Valid DocumentSearchRequest request) {
        DocumentSearchResponse response = service.searchDocuments(request);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{documentId}/download")
    @Authenticated
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@PathParam("documentId") UUID documentId) {
        StreamingOutput stream = output -> {
            try (var inputStream = service.download(documentId)) {
                inputStream.transferTo(output);
            }
        };
        return Response.ok(stream).build();
    }

    @DELETE
    @Path("/{documentId}")
    @RolesAllowed("admin")
    public Response deleteDocument(@PathParam("documentId") UUID documentId) {
        DeleteDocumentResponse response = service.deleteDocument(documentId);
        return Response.ok(response).build();
    }

}