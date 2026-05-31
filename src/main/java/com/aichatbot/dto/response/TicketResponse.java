package com.aichatbot.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private String attachmentUrl;
    private Long userId;
    private String username;
    private List<CommentResponse> comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
