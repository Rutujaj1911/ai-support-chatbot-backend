package com.aichatbot.service;

import com.aichatbot.dto.request.LoginRequest;
import com.aichatbot.dto.request.RegisterRequest;
import com.aichatbot.dto.response.JwtResponse;
import com.aichatbot.dto.response.ApiResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request);
    ApiResponse<?> register(RegisterRequest request);
}
