package com.solvex.ragflow.repository;

import com.solvex.ragflow.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
