package com.solvex.ragflow.repository;

import com.solvex.ragflow.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
