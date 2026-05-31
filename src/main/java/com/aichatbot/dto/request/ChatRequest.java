package com.aichatbot.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ChatRequest {
    @NotBlank(message = "Message is required")
    private String message;

    private String sessionId;
}
