package com.aichatbot.service.impl;

import com.aichatbot.dto.request.ChatRequest;
import com.aichatbot.dto.response.ChatResponse;
import com.aichatbot.entity.ChatMessage;
import com.aichatbot.entity.ChatMessage.MessageRole;
import com.aichatbot.entity.FAQ;
import com.aichatbot.entity.User;
import com.aichatbot.exception.ResourceNotFoundException;
import com.aichatbot.repository.ChatMessageRepository;
import com.aichatbot.repository.FAQRepository;
import com.aichatbot.repository.UserRepository;
import com.aichatbot.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatMessageRepository chatRepo;
    private final UserRepository userRepo;
    private final FAQRepository faqRepo;
    private final WebClient.Builder webClientBuilder;
    @Value("${groq.api.key:}") private String groqKey;
    @Value("${groq.api.url:}") private String groqUrl;
    @Value("${groq.model:llama3-8b-8192}") private String groqModel;

    @Override
    @Transactional
    public ChatResponse sendMessage(ChatRequest request, String username) {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String sessionId = (request.getSessionId() != null && !request.getSessionId().isEmpty())
            ? request.getSessionId() : UUID.randomUUID().toString();

        ChatMessage userMsg = ChatMessage.builder()
            .user(user).sessionId(sessionId)
            .role(MessageRole.USER).content(request.getMessage()).build();
        chatRepo.save(userMsg);

        String faqContext = buildFAQContext(request.getMessage());

        String aiReply = getAIResponse(request.getMessage(), faqContext, username, sessionId);

        ChatMessage aiMsg = ChatMessage.builder()
            .user(user).sessionId(sessionId)
            .role(MessageRole.ASSISTANT).content(aiReply).build();
        chatRepo.save(aiMsg);

        return mapToResponse(aiMsg);
    }

    private String buildFAQContext(String userMessage) {
        List<FAQ> faqs = faqRepo.searchFAQs(userMessage);
        if (faqs.isEmpty()) faqs = faqRepo.findByActiveTrue();
        if (faqs.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("Relevant FAQs:\n");
        faqs.stream().limit(5).forEach(f ->
            sb.append("Q: ").append(f.getQuestion()).append("\nA: ").append(f.getAnswer()).append("\n\n"));
        return sb.toString();
    }

    private String getAIResponse(String message, String faqContext, String username, String sessionId) {
        try {
            String systemPrompt = "You are a helpful customer support assistant. " +
                    "Answer questions based on the provided FAQ context. " +
                    "If you cannot find a relevant answer, suggest creating a support ticket.\n\n" + faqContext;

            return callGroq(message, systemPrompt);  // ← Groq use kar

        } catch (Exception e) {
            log.error("AI API error: {}", e.getMessage());
            return "I'm sorry, I'm having trouble connecting. Please create a support ticket.";
        }
    }

    private String callGroq(String message, String systemPrompt) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", groqModel);
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", message)
        ));
        body.put("max_tokens", 500);
        body.put("temperature", 0.7);

        Map response = webClientBuilder.build().post()
                .uri(groqUrl)
                .header("Authorization", "Bearer " + groqKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List choices = (List) response.get("choices");
        Map choice = (Map) choices.get(0);
        Map msg = (Map) choice.get("message");
        return (String) msg.get("content");
    }



    @Override
    public List<ChatResponse> getChatHistory(String username, String sessionId) {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return chatRepo.findByUserAndSessionIdOrderByCreatedAtAsc(user, sessionId)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<String> getUserSessions(String username) {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return chatRepo.findDistinctSessionIdsByUser(user);
    }

    @Override
    public List<ChatResponse> getAllMessages() {
        return chatRepo.findAllOrderByCreatedAtDesc()
            .stream().limit(50).map(this::mapToResponse).collect(Collectors.toList());
    }

    private ChatResponse mapToResponse(ChatMessage msg) {
        return ChatResponse.builder()
            .id(msg.getId()).sessionId(msg.getSessionId())
            .role(msg.getRole().name()).content(msg.getContent())
            .createdAt(msg.getCreatedAt()).build();
    }
}
