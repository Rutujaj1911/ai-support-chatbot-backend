package com.aichatbot.controller;

import com.aichatbot.dto.request.TicketRequest;
import com.aichatbot.dto.request.TicketStatusRequest;
import com.aichatbot.dto.response.ApiResponse;
import com.aichatbot.dto.response.TicketResponse;
import com.aichatbot.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Support Ticket APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class TicketController {
    private final TicketService ticketService;

    @PostMapping
    @Operation(summary = "Create support ticket")
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(
            @Valid @RequestBody TicketRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Ticket created",
            ticketService.createTicket(request, auth.getName())));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my tickets")
    public ResponseEntity<ApiResponse<Page<TicketResponse>>> getMyTickets(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success("My tickets",
            ticketService.getUserTickets(auth.getName(), pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Ticket fetched", ticketService.getTicketById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update ticket")
    public ResponseEntity<ApiResponse<TicketResponse>> updateTicket(
            @PathVariable Long id, @Valid @RequestBody TicketRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Ticket updated",
            ticketService.updateTicket(id, request, auth.getName())));
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add comment to ticket")
    public ResponseEntity<ApiResponse<TicketResponse>> addComment(
            @PathVariable Long id, @RequestParam String content, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Comment added",
            ticketService.addComment(id, content, auth.getName())));
    }

    @PostMapping("/{id}/attachment")
    @Operation(summary = "Upload attachment to ticket")
    public ResponseEntity<ApiResponse<String>> uploadAttachment(
            @PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        String url = ticketService.saveAttachment(id, file.getBytes(), file.getOriginalFilename());
        return ResponseEntity.ok(ApiResponse.success("File uploaded", url));
    }
}
