package com.aichatbot.dto.request;

import com.aichatbot.entity.SupportTicket.Priority;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TicketRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private Priority priority = Priority.MEDIUM;
}
