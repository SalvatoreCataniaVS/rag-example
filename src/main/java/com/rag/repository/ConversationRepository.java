package com.rag.repository;

import com.rag.repository.entity.Conversation;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ConversationRepository implements PanacheRepositoryBase<Conversation, UUID> {

    public List<Conversation> findByUserId(UUID userId) {
        return find("userId = :userId ORDER BY updatedAt DESC",
                java.util.Collections.singletonMap("userId", userId)).list();
    }

}