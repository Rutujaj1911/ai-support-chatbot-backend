package com.aichatbot.service;

import com.aichatbot.dto.request.TicketRequest;
import com.aichatbot.dto.request.TicketStatusRequest;
import com.aichatbot.dto.response.TicketResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketService {
    TicketResponse createTicket(TicketRequest request, String username);
    TicketResponse updateTicket(Long id, TicketRequest request, String username);
    TicketResponse updateTicketStatus(Long id, TicketStatusRequest request);
    void deleteTicket(Long id);
    TicketResponse getTicketById(Long id);
    Page<TicketResponse> getUserTickets(String username, Pageable pageable);
    Page<TicketResponse> getAllTickets(Pageable pageable);
    Page<TicketResponse> searchTickets(String keyword, Pageable pageable);
    TicketResponse addComment(Long ticketId, String content, String username);
    String saveAttachment(Long ticketId, byte[] fileBytes, String fileName);
}
