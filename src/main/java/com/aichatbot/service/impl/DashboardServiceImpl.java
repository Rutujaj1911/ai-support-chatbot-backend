package com.aichatbot.service.impl;

import com.aichatbot.dto.response.*;
import com.aichatbot.entity.SupportTicket.Status;
import com.aichatbot.repository.*;
import com.aichatbot.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final UserRepository userRepo;
    private final SupportTicketRepository ticketRepo;
    private final ChatMessageRepository chatRepo;

    @Override
    public DashboardResponse getDashboardStats() {
        List<TicketResponse> recentTickets = ticketRepo
            .findRecentTickets(PageRequest.of(0, 5))
            .stream().map(t -> TicketResponse.builder()
                .id(t.getId()).title(t.getTitle()).status(t.getStatus().name())
                .priority(t.getPriority().name()).username(t.getUser().getUsername())
                .createdAt(t.getCreatedAt()).build())
            .collect(Collectors.toList());

        List<ChatResponse> recentMessages = chatRepo.findAllOrderByCreatedAtDesc()
            .stream().limit(5).map(m -> ChatResponse.builder()
                .id(m.getId()).role(m.getRole().name()).content(m.getContent())
                .createdAt(m.getCreatedAt()).build())
            .collect(Collectors.toList());

        return DashboardResponse.builder()
            .totalUsers(userRepo.count())
            .totalTickets(ticketRepo.count())
            .openTickets(ticketRepo.countByStatus(Status.OPEN))
            .resolvedTickets(ticketRepo.countByStatus(Status.RESOLVED))
            .closedTickets(ticketRepo.countByStatus(Status.CLOSED))
            .totalMessages(chatRepo.count())
            .recentTickets(recentTickets)
            .recentMessages(recentMessages)
            .build();
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepo.findAll().stream().map(u -> UserResponse.builder()
            .id(u.getId()).username(u.getUsername()).email(u.getEmail())
            .fullName(u.getFullName()).phone(u.getPhone()).enabled(u.isEnabled())
            .roles(u.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()))
            .createdAt(u.getCreatedAt()).build())
            .collect(Collectors.toList());
    }
}
