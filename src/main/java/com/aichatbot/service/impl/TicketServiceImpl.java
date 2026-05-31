package com.aichatbot.service.impl;

import com.aichatbot.dto.request.TicketRequest;
import com.aichatbot.dto.request.TicketStatusRequest;
import com.aichatbot.dto.response.CommentResponse;
import com.aichatbot.dto.response.TicketResponse;
import com.aichatbot.entity.*;
import com.aichatbot.exception.ResourceNotFoundException;
import com.aichatbot.repository.*;
import com.aichatbot.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final SupportTicketRepository ticketRepo;
    private final UserRepository userRepo;
    private final TicketCommentRepository commentRepo;

    @Value("${file.upload.dir:uploads}") private String uploadDir;

    @Override
    @Transactional
    public TicketResponse createTicket(TicketRequest req, String username) {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SupportTicket ticket = SupportTicket.builder()
            .title(req.getTitle()).description(req.getDescription())
            .priority(req.getPriority()).user(user).build();
        return mapToResponse(ticketRepo.save(ticket));
    }

    @Override
    @Transactional
    public TicketResponse updateTicket(Long id, TicketRequest req, String username) {
        SupportTicket ticket = ticketRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + id));
        ticket.setTitle(req.getTitle());
        ticket.setDescription(req.getDescription());
        ticket.setPriority(req.getPriority());
        return mapToResponse(ticketRepo.save(ticket));
    }

    @Override
    @Transactional
    public TicketResponse updateTicketStatus(Long id, TicketStatusRequest req) {
        SupportTicket ticket = ticketRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + id));
        ticket.setStatus(req.getStatus());
        return mapToResponse(ticketRepo.save(ticket));
    }

    @Override
    public void deleteTicket(Long id) {
        if (!ticketRepo.existsById(id)) throw new ResourceNotFoundException("Ticket not found: " + id);
        ticketRepo.deleteById(id);
    }

    @Override
    public TicketResponse getTicketById(Long id) {
        return mapToResponse(ticketRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + id)));
    }

    @Override
    public Page<TicketResponse> getUserTickets(String username, Pageable pageable) {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ticketRepo.findByUser(user, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<TicketResponse> getAllTickets(Pageable pageable) {
        return ticketRepo.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public Page<TicketResponse> searchTickets(String keyword, Pageable pageable) {
        return ticketRepo.searchTickets(keyword, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public TicketResponse addComment(Long ticketId, String content, String username) {
        SupportTicket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        TicketComment comment = TicketComment.builder()
            .ticket(ticket).user(user).content(content).build();
        commentRepo.save(comment);
        return mapToResponse(ticketRepo.findById(ticketId).get());
    }

    @Override
    public String saveAttachment(Long ticketId, byte[] fileBytes, String fileName) {
        try {
            Path uploadPath = Paths.get(uploadDir, "tickets", ticketId.toString());
            Files.createDirectories(uploadPath);
            String uniqueName = UUID.randomUUID() + "_" + fileName;
            Files.write(uploadPath.resolve(uniqueName), fileBytes);
            String url = "/uploads/tickets/" + ticketId + "/" + uniqueName;
            SupportTicket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
            ticket.setAttachmentUrl(url);
            ticketRepo.save(ticket);
            return url;
        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    private TicketResponse mapToResponse(SupportTicket t) {
        List<CommentResponse> comments = t.getComments() == null ? List.of() :
            t.getComments().stream().map(c -> CommentResponse.builder()
                .id(c.getId()).content(c.getContent())
                .userId(c.getUser().getId()).username(c.getUser().getUsername())
                .createdAt(c.getCreatedAt()).build()).collect(Collectors.toList());
        return TicketResponse.builder()
            .id(t.getId()).title(t.getTitle()).description(t.getDescription())
            .priority(t.getPriority().name()).status(t.getStatus().name())
            .attachmentUrl(t.getAttachmentUrl())
            .userId(t.getUser().getId()).username(t.getUser().getUsername())
            .comments(comments).createdAt(t.getCreatedAt()).updatedAt(t.getUpdatedAt()).build();
    }
}
