package com.rag.rest;

import com.rag.api.predict.ChatRequest;
import com.rag.api.predict.ChatResponse;
import com.rag.api.predict.ConversationDetailResponse;
import com.rag.api.predict.ConversationSummaryResponse;
import com.rag.service.ConversationService;
import com.rag.service.PredictService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/predict")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PredictResource {

    @Inject
    PredictService predictService;

    @Inject
    ConversationService conversationService;

    // ─── Chat ─────────────────────────────────────────────────────────────────

    @POST
    @Path("/chat")
    @Authenticated
    public Response chat(@Valid ChatRequest request) {
        // TODO: replace hardcoded UUID with authenticated user id once security is active
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        ChatResponse response = predictService.chat(request, userId);
        return Response.ok(response).build();
    }

    @POST
    @Path("/chat/incognito")
    @Authenticated
    public Response chatIncognito(@Valid ChatRequest request) {
        ChatResponse response = predictService.chatIncognito(request);
        return Response.ok(response).build();
    }

    // ─── Conversations ────────────────────────────────────────────────────────

    @GET
    @Path("/conversations")
    @Authenticated
    public Response listConversations() {
        // TODO: replace hardcoded UUID with authenticated user id once security is active
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        ConversationSummaryResponse response = conversationService.listConversations(userId);
        return Response.ok(response).build();
    }

    @GET
    @Path("/conversations/{conversationId}")
    @Authenticated
    public Response getConversation(@PathParam("conversationId") UUID conversationId) {
        ConversationDetailResponse response = conversationService.getConversation(conversationId);
        return Response.ok(response).build();
    }

}