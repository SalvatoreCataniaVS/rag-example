package com.rag.rest;

import com.rag.service.PredictService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/predict")
public class PredictResource {

    @Inject
    private PredictService service;

}
