package com.bookverse.service;

import com.bookverse.dto.AuthResponseDTO;
import com.bookverse.dto.LoginRequestDTO;
import com.bookverse.dto.RefreshRequestDTO;
import com.bookverse.dto.RegisterRequestDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO dto);
    AuthResponseDTO login(LoginRequestDTO dto);
    AuthResponseDTO refresh(RefreshRequestDTO dto);
    void logout(RefreshRequestDTO dto);
}
