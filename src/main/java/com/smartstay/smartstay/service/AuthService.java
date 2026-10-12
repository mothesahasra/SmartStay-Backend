package com.smartstay.smartstay.service;

import com.smartstay.smartstay.dto.request.LoginRequest;
import com.smartstay.smartstay.dto.request.RegisterRequest;
import com.smartstay.smartstay.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken);
}
