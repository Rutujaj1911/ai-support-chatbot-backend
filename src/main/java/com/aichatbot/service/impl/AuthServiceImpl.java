package com.aichatbot.service.impl;

import com.aichatbot.dto.request.LoginRequest;
import com.aichatbot.dto.request.RegisterRequest;
import com.aichatbot.dto.response.ApiResponse;
import com.aichatbot.dto.response.JwtResponse;
import com.aichatbot.entity.Role;
import com.aichatbot.entity.Role.ERole;
import com.aichatbot.entity.User;
import com.aichatbot.exception.BadRequestException;
import com.aichatbot.repository.RoleRepository;
import com.aichatbot.repository.UserRepository;
import com.aichatbot.security.UserDetailsImpl;
import com.aichatbot.security.jwt.JwtUtils;
import com.aichatbot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authManager;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @Override
    public JwtResponse login(LoginRequest request) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = jwtUtils.generateJwtToken(auth);
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
            .map(a -> a.getAuthority()).collect(Collectors.toList());
        return JwtResponse.builder()
            .token(jwt).type("Bearer")
            .id(userDetails.getId())
            .username(userDetails.getUsername())
            .email(userDetails.getEmail())
            .roles(roles).build();
    }

    @Override
    @Transactional
    public ApiResponse<?> register(RegisterRequest request) {
        if (userRepo.existsByUsername(request.getUsername()))
            throw new BadRequestException("Username already taken!");
        if (userRepo.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already in use!");
        Role userRole = roleRepo.findByName(ERole.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Role not found"));
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(encoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .phone(request.getPhone())
            .roles(Set.of(userRole)).build();
        userRepo.save(user);
        return ApiResponse.success("User registered successfully!", null);
    }
}
