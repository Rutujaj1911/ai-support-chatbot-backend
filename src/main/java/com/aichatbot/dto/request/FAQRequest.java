package com.aichatbot.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FAQRequest {
    @NotBlank(message = "Question is required")
    private String question;

    @NotBlank(message = "Answer is required")
    private String answer;

    private String category;
    private boolean active = true;
}
