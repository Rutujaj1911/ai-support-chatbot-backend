package com.aichatbot.controller;

import com.aichatbot.dto.request.ChatRequest;
import com.aichatbot.dto.response.ApiResponse;
import com.aichatbot.dto.response.ChatResponse;
import com.aichatbot.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "AI Chatbot APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/send")
    @Operation(summary = "Send message to AI chatbot")
    public ResponseEntity<ApiResponse<ChatResponse>> sendMessage(
            @Valid @RequestBody ChatRequest request, Authentication auth) {
        ChatResponse response = chatService.sendMessage(request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Message sent", response));
    }

    @GetMapping("/history/{sessionId}")
    @Operation(summary = "Get chat history for a session")
    public ResponseEntity<ApiResponse<List<ChatResponse>>> getChatHistory(
            @PathVariable String sessionId, Authentication auth) {
        List<ChatResponse> history = chatService.getChatHistory(auth.getName(), sessionId);
        return ResponseEntity.ok(ApiResponse.success("Chat history fetched", history));
    }

    @GetMapping("/sessions")
    @Operation(summary = "Get all user chat sessions")
    public ResponseEntity<ApiResponse<List<String>>> getSessions(Authentication auth) {
        List<String> sessions = chatService.getUserSessions(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Sessions fetched", sessions));
    }
}
