package com.aichatbot.service.impl;

import com.aichatbot.dto.request.FAQRequest;
import com.aichatbot.dto.response.FAQResponse;
import com.aichatbot.entity.FAQ;
import com.aichatbot.exception.ResourceNotFoundException;
import com.aichatbot.repository.FAQRepository;
import com.aichatbot.service.FAQService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FAQServiceImpl implements FAQService {
    private final FAQRepository faqRepo;

    @Override
    public FAQResponse createFAQ(FAQRequest req) {
        FAQ faq = FAQ.builder().question(req.getQuestion()).answer(req.getAnswer())
            .category(req.getCategory()).active(req.isActive()).build();
        return mapToResponse(faqRepo.save(faq));
    }

    @Override
    public FAQResponse updateFAQ(Long id, FAQRequest req) {
        FAQ faq = faqRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("FAQ not found: " + id));
        faq.setQuestion(req.getQuestion());
        faq.setAnswer(req.getAnswer());
        faq.setCategory(req.getCategory());
        faq.setActive(req.isActive());
        return mapToResponse(faqRepo.save(faq));
    }

    @Override
    public void deleteFAQ(Long id) {
        if (!faqRepo.existsById(id)) throw new ResourceNotFoundException("FAQ not found: " + id);
        faqRepo.deleteById(id);
    }

    @Override
    public FAQResponse getFAQById(Long id) {
        return mapToResponse(faqRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("FAQ not found: " + id)));
    }

    @Override
    public List<FAQResponse> getAllFAQs() {
        return faqRepo.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<FAQResponse> getActiveFAQs() {
        return faqRepo.findByActiveTrue().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<FAQResponse> searchFAQs(String keyword) {
        return faqRepo.searchFAQs(keyword).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private FAQResponse mapToResponse(FAQ faq) {
        return FAQResponse.builder().id(faq.getId()).question(faq.getQuestion())
            .answer(faq.getAnswer()).category(faq.getCategory()).active(faq.isActive())
            .createdAt(faq.getCreatedAt()).updatedAt(faq.getUpdatedAt()).build();
    }
}
