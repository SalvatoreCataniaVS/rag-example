package com.rag.rest;

import com.rag.service.DocumentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/document")
public class DocumentResource {

    @Inject
    private DocumentService service;

}
