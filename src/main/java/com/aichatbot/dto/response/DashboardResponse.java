package com.aichatbot.dto.response;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {
    private long totalUsers;
    private long totalTickets;
    private long openTickets;
    private long resolvedTickets;
    private long closedTickets;
    private long totalMessages;
    private List<TicketResponse> recentTickets;
    private List<ChatResponse> recentMessages;
}
