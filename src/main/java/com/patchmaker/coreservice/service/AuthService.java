package com.patchmaker.coreservice.service;

import com.patchmaker.coreservice.dto.request.LoginRequest;
import com.patchmaker.coreservice.dto.request.RegisterRequest;
import com.patchmaker.coreservice.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}