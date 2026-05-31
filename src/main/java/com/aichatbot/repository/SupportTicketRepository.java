package com.aichatbot.repository;

import com.aichatbot.entity.SupportTicket;
import com.aichatbot.entity.SupportTicket.Status;
import com.aichatbot.entity.SupportTicket.Priority;
import com.aichatbot.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    Page<SupportTicket> findByUser(User user, Pageable pageable);
    Page<SupportTicket> findByStatus(Status status, Pageable pageable);
    Page<SupportTicket> findByUserAndStatus(User user, Status status, Pageable pageable);

    @Query("SELECT t FROM SupportTicket t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<SupportTicket> searchTickets(String keyword, Pageable pageable);

    long countByStatus(Status status);
    long countByUser(User user);

    @Query("SELECT t FROM SupportTicket t ORDER BY t.createdAt DESC")
    List<SupportTicket> findRecentTickets(Pageable pageable);
}
