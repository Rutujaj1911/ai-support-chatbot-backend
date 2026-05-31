package com.aichatbot.controller;

import com.aichatbot.dto.request.FAQRequest;
import com.aichatbot.dto.response.ApiResponse;
import com.aichatbot.dto.response.FAQResponse;
import com.aichatbot.service.FAQService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
@RequiredArgsConstructor
@Tag(name = "FAQ", description = "FAQ Management APIs")
public class FAQController {
    private final FAQService faqService;

    @GetMapping("/public")
    @Operation(summary = "Get all active FAQs (public)")
    public ResponseEntity<ApiResponse<List<FAQResponse>>> getActiveFAQs() {
        return ResponseEntity.ok(ApiResponse.success("FAQs fetched", faqService.getActiveFAQs()));
    }

    @GetMapping("/public/search")
    @Operation(summary = "Search FAQs (public)")
    public ResponseEntity<ApiResponse<List<FAQResponse>>> searchFAQs(@RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success("Search results", faqService.searchFAQs(keyword)));
    }

    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all FAQs (admin)")
    public ResponseEntity<ApiResponse<List<FAQResponse>>> getAllFAQs() {
        return ResponseEntity.ok(ApiResponse.success("All FAQs fetched", faqService.getAllFAQs()));
    }

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create FAQ")
    public ResponseEntity<ApiResponse<FAQResponse>> createFAQ(@Valid @RequestBody FAQRequest request) {
        return ResponseEntity.ok(ApiResponse.success("FAQ created", faqService.createFAQ(request)));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update FAQ")
    public ResponseEntity<ApiResponse<FAQResponse>> updateFAQ(
            @PathVariable Long id, @Valid @RequestBody FAQRequest request) {
        return ResponseEntity.ok(ApiResponse.success("FAQ updated", faqService.updateFAQ(id, request)));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete FAQ")
    public ResponseEntity<ApiResponse<?>> deleteFAQ(@PathVariable Long id) {
        faqService.deleteFAQ(id);
        return ResponseEntity.ok(ApiResponse.success("FAQ deleted", null));
    }
}
