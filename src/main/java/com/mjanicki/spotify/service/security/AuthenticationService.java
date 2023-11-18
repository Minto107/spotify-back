package com.mjanicki.spotify.service.security;

import com.mjanicki.spotify.dao.security.JwtResponse;
import com.mjanicki.spotify.dao.security.LoginRequest;
import com.mjanicki.spotify.dao.security.RegisterRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    JwtResponse login(LoginRequest login, HttpServletResponse response);

    JwtResponse register(RegisterRequest register, HttpServletResponse response);

    void logout(HttpServletResponse response);

    JwtResponse getUser(HttpServletRequest request);
}
