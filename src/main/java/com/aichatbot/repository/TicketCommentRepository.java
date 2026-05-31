package com.aichatbot.repository;

import com.aichatbot.entity.TicketComment;
import com.aichatbot.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
    List<TicketComment> findByTicketOrderByCreatedAtAsc(SupportTicket ticket);
}
