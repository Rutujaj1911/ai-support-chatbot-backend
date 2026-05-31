package com.aichatbot.dto.request;

import com.aichatbot.entity.SupportTicket.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketStatusRequest {
    @NotNull(message = "Status is required")
    private Status status;
}
