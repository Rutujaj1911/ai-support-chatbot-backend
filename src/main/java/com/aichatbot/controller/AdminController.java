package com.aichatbot.controller;

import com.aichatbot.dto.request.TicketStatusRequest;
import com.aichatbot.dto.response.*;
import com.aichatbot.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin Dashboard APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {
    private final DashboardService dashboardService;
    private final TicketService ticketService;
    private final ChatService chatService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard data", dashboardService.getDashboardStats()));
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("Users fetched", dashboardService.getAllUsers()));
    }

    @GetMapping("/tickets")
    @Operation(summary = "Get all tickets")
    public ResponseEntity<ApiResponse<Page<TicketResponse>>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TicketResponse> tickets = (search != null && !search.isEmpty())
            ? ticketService.searchTickets(search, pageable)
            : ticketService.getAllTickets(pageable);
        return ResponseEntity.ok(ApiResponse.success("Tickets fetched", tickets));
    }

    @PutMapping("/tickets/{id}/status")
    @Operation(summary = "Update ticket status")
    public ResponseEntity<ApiResponse<TicketResponse>> updateTicketStatus(
            @PathVariable Long id, @Valid @RequestBody TicketStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Ticket status updated",
            ticketService.updateTicketStatus(id, request)));
    }

    @DeleteMapping("/tickets/{id}")
    @Operation(summary = "Delete ticket")
    public ResponseEntity<ApiResponse<?>> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok(ApiResponse.success("Ticket deleted", null));
    }

    @GetMapping("/messages")
    @Operation(summary = "Get all chat messages")
    public ResponseEntity<ApiResponse<List<ChatResponse>>> getAllMessages() {
        return ResponseEntity.ok(ApiResponse.success("Messages fetched", chatService.getAllMessages()));
    }
}
