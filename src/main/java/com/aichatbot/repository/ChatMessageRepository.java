package com.aichatbot.repository;

import com.aichatbot.entity.ChatMessage;
import com.aichatbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserAndSessionIdOrderByCreatedAtAsc(User user, String sessionId);
    List<ChatMessage> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT DISTINCT m.sessionId FROM ChatMessage m WHERE m.user = :user ORDER BY m.sessionId DESC")
    List<String> findDistinctSessionIdsByUser(User user);

    @Query("SELECT m FROM ChatMessage m ORDER BY m.createdAt DESC")
    List<ChatMessage> findAllOrderByCreatedAtDesc();

    long countByUser(User user);
}
