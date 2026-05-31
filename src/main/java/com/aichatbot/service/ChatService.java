package com.aichatbot.service;

import com.aichatbot.dto.request.ChatRequest;
import com.aichatbot.dto.response.ChatResponse;
import java.util.List;

public interface ChatService {
    ChatResponse sendMessage(ChatRequest request, String username);
    List<ChatResponse> getChatHistory(String username, String sessionId);
    List<String> getUserSessions(String username);
    List<ChatResponse> getAllMessages();
}
