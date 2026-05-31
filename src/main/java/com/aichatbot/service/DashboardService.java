package com.aichatbot.service;

import com.aichatbot.dto.response.DashboardResponse;
import com.aichatbot.dto.response.UserResponse;
import java.util.List;

public interface DashboardService {
    DashboardResponse getDashboardStats();
    List<UserResponse> getAllUsers();
}
