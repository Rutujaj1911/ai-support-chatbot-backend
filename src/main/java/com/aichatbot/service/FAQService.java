package com.aichatbot.service;

import com.aichatbot.dto.request.FAQRequest;
import com.aichatbot.dto.response.FAQResponse;
import java.util.List;

public interface FAQService {
    FAQResponse createFAQ(FAQRequest request);
    FAQResponse updateFAQ(Long id, FAQRequest request);
    void deleteFAQ(Long id);
    FAQResponse getFAQById(Long id);
    List<FAQResponse> getAllFAQs();
    List<FAQResponse> getActiveFAQs();
    List<FAQResponse> searchFAQs(String keyword);
}
